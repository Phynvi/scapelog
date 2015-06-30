package com.scapelog.client.loader;

import com.google.common.collect.ImmutableList;
import com.scapelog.agent.RSClassTransformer;
import com.scapelog.client.ScapeLog;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.client.event.impl.LoadingEvent;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.OperationAttributes;
import com.scapelog.client.loader.analyser.ReflectionAnalyser;
import com.scapelog.client.loader.analyser.ReflectionOperation;
import com.scapelog.client.loader.analyser.impl.ClassLoaderAnalyser;
import com.scapelog.client.loader.analyser.impl.GameMessageAnalyser;
import com.scapelog.client.loader.analyser.impl.IdleResetAnalyser;
import com.scapelog.client.loader.analyser.impl.MultiplierAnalyser;
import com.scapelog.client.loader.analyser.impl.SkillAnalyser;
import com.scapelog.client.loader.analyser.impl.StringFieldAnalyser;
import com.scapelog.client.loader.analyser.impl.VariableAnalyser;
import com.scapelog.client.loader.analyser.impl.reflection.MenuBuilderAnalyser;
import com.scapelog.client.loader.analyser.impl.reflection.MobAnalyser;
import com.scapelog.client.loader.analyser.impl.reflection.NpcAnalyser;
import com.scapelog.client.loader.analyser.impl.reflection.PlayerAnalyser;
import com.scapelog.client.loader.analyser.impl.reflection.WorldTypeAnalyser;
import com.scapelog.client.loader.archive.ClassNodeArchive;
import com.scapelog.client.loader.archive.JarArchive;
import com.scapelog.client.loader.archive.JarArchiveClassLoader;
import com.scapelog.client.model.Language;
import com.scapelog.client.model.WorldList;
import com.scapelog.client.reflection.ReflectedField;
import com.scapelog.client.ui.util.WebUtils;
import com.scapelog.client.util.Debug;
import org.objectweb.asm.tree.ClassNode;

import java.applet.Applet;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ClientLoader {
	private final Pattern WORLD_ID_PATTERN = Pattern.compile("world(.*?)\\.");
	private final Pattern LOBBY_ID_PATTERN = Pattern.compile("lobby(.*?)\\.");

	private final Optional<WorldList> world;
	private final Language language;

	private final List<Analyser> analysers;
	private final List<ReflectionAnalyser> reflectionAnalysers;

	public ClientLoader(Optional<WorldList> world, Language language, ImmutableList<Analyser> analysers, ImmutableList<ReflectionAnalyser> reflectionAnalysers) {
		this.world = world;
		this.language = language;
		this.analysers = analysers;
		this.reflectionAnalysers = reflectionAnalysers;
	}

	public static ClientLoader create(Optional<WorldList> world, Language language) {
		ImmutableList.Builder<Analyser> analyserBuilder = new ImmutableList.Builder<>();
		ImmutableList.Builder<ReflectionAnalyser> reflectionBuilder = new ImmutableList.Builder<>();
		analyserBuilder.add(
				// keep these analysers first
				new MultiplierAnalyser(),
				new ClassLoaderAnalyser(),
				new StringFieldAnalyser(),
				//new LibraryLoaderAnalyser(),

				new SkillAnalyser(),
				new IdleResetAnalyser(),
				new VariableAnalyser(),
				new GameMessageAnalyser()
//				new CameraZoomAnalyser()
//				new VariableAnalyser()
		);
		reflectionBuilder.add(
				new WorldTypeAnalyser(),
				new MenuBuilderAnalyser(),
				new PlayerAnalyser(),
				new MobAnalyser(),
				new NpcAnalyser()
		);
		return new ClientLoader(world, language, analyserBuilder.build(), reflectionBuilder.build());
	}

	public Applet load() throws Exception {
		String outputPath = System.getProperty("java.io.tmpdir") + "/.scapelog_cache";

		print("Loading config...");
		JavConfig config = new JavConfig(this, world, language);
		if (!config.load()) {
			print("-> Failed to load client config, please try again.");
			printDiagnosticsInstructions();
			return null;
		}
		String gamePack = config.getConfig(JavConfig.GAMEPACK_NAME);
		String appletClass = config.getConfig(JavConfig.MAIN_CLASS);
		String codebase = config.getConfig(JavConfig.CODE_BASE);
		print("-> Loaded");

		print("Downloading client...");
		Path jar;
		try {
			jar = WebUtils.download(codebase, gamePack, outputPath, cleanGamepackName(gamePack));
		} catch (Exception e) {
			print("-> Failed to download the client");
			print(e.toString());
			printDiagnosticsInstructions();
			e.printStackTrace();
			return null;
		}
		print("-> Downloaded");

		GamePackArchives gamePackArchives = getGamePackArchive(jar, config);
		JarArchive archive = gamePackArchives.getGamepackArchive();
		if (ScapeLog.isAgentEnabled()) {
			print("Analysing client...");
			try {
				archive = analyse(jar, config, gamePackArchives);
			} catch (CryptographyException | IOException e) {
				print("-> Failed to analyse the client, retrying after redownloading.");
				print(e.toString());
				printDiagnosticsInstructions();
				jar = WebUtils.download(codebase, gamePack, outputPath, gamePack.replace("/", ""));
				archive = analyse(jar, config, gamePackArchives);
			}
			print("-> Analysed");

			if (archive == null) {
				print("-> Something unexpected and serious happened while loading the client");
				printDiagnosticsInstructions();
				return null;
			}
		}

		boolean isDefault = !world.isPresent();
		print((isDefault ? "Received default" : "Using") + " world " + getWorldId(codebase) + ", lobby " + findLobbyId(config.getParameters()));

		JarArchiveClassLoader classLoader = JarArchiveClassLoader.create(archive);
		Applet applet = (Applet) classLoader.loadClass(appletClass.replace(".class", "")).newInstance();
		applet.setStub(new ClientAppletStub(config, new URL(codebase)));

		return applet;
	}

	private void printDiagnosticsInstructions() {
		print("");
		print("Please use the key shortcut CTRL+SHIFT+INSERT to copy the error diagnostics to your clipboard");
		print("and then deliver the message to the developers");
		print("Meanwhile you can restart ScapeLog to see if the problem is persistent");
	}

	private GamePackArchives getGamePackArchive(Path file, JavConfig config) throws Exception {
		GameClientModifier modifier = new GameClientModifier();
		return modifier.unpack(config, file);
	}

	private JarArchive analyse(Path file, JavConfig config, GamePackArchives archives) throws IOException, CryptographyException {
		// The gamepack's non-decrypted classes
		ClassNodeArchive nodeArchive = new ClassNodeArchive(archives.getGamepackArchive());
		nodeArchive.addClassNodes();
		Collection<ClassNode> classNodes = nodeArchive.toCollection();

		OperationAttributes attributes = new OperationAttributes();
		AnalysingOperation analysingOperation = new AnalysingOperation(attributes);
		ReflectionOperation reflectionOperation = new ReflectionOperation(attributes);

		analyse(classNodes, analysingOperation, reflectionOperation);

		// The decrypted classes
		nodeArchive = new ClassNodeArchive(archives.getClientArchive());
		nodeArchive.addClassNodes();
		classNodes = nodeArchive.toCollection();
		analyse(classNodes, analysingOperation, reflectionOperation);

		checkRequiredFields();

		// todo: temp
		/*if (ScapeLog.debug) {
			classNodes.forEach(ClassNodeUtils::dumpClass);
		}*/

		RSClassTransformer.addInjections(analysingOperation.getClassInjections());
		return archives.getGamepackArchive();
	}

	private void analyse(Collection<ClassNode> classNodes, AnalysingOperation analysingOperation, ReflectionOperation reflectionOperation) {
		// run the injection analysers
		for (Analyser analyser : analysers) {
			try {
				analyser.analyse(classNodes, analysingOperation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// run the reflection analysers
		for (ReflectionAnalyser analyser : reflectionAnalysers) {
			try {
				analyser.analyse(classNodes, reflectionOperation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void checkRequiredFields() {
		for (ReflectionAnalyser analyser : reflectionAnalysers) {
			ReflectedField[] requiredFields = analyser.getRequiredFields();

			for (ReflectedField reflectedField : requiredFields) {
				Debug.println("[%s] className=%s, fieldName=%s",
						reflectedField.getClass().hashCode(),
						reflectedField.getClassName().orElse("MISSING"),
						reflectedField.getFieldName().orElse("MISSING"));
			}
		}
	}

	protected void print(String message) {
		ClientEventDispatcher.fireEvent(new LoadingEvent(message));
	}

	private int getWorldId(String codebase) {
		Matcher matcher = WORLD_ID_PATTERN.matcher(codebase);
		if (matcher.find()) {
			String worldId = matcher.group(1);
			return Integer.parseInt(worldId);
		}
		return -1;
	}

	private String findLobbyId(Map<String, String> parameters) {
		for (String value : parameters.values()) {
			Matcher matcher = LOBBY_ID_PATTERN.matcher(value);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return "n/a";
	}

	private String cleanGamepackName(String name) {
		return name.replaceAll("[^A-Za-z0-9\\.]", "");
	}

}