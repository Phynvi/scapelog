package com.scapelog.client.loader;

import com.google.common.collect.ImmutableList;
import com.scapelog.agent.RSClassTransformer;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.client.event.impl.LoadingEvent;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.impl.ClassLoaderAnalyser;
import com.scapelog.client.loader.analyser.impl.GameMessageAnalyser;
import com.scapelog.client.loader.analyser.impl.IdleResetAnalyser;
import com.scapelog.client.loader.analyser.impl.MultiplierAnalyser;
import com.scapelog.client.loader.analyser.impl.ReflectionAnalyser;
import com.scapelog.client.loader.analyser.impl.SkillAnalyser;
import com.scapelog.client.loader.analyser.impl.StringFieldAnalyser;
import com.scapelog.client.loader.analyser.impl.VariableAnalyser;
import com.scapelog.client.loader.analyser.impl.reflection.PlayerAnalyser;
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
				new MultiplierAnalyser(),
				new ClassLoaderAnalyser(),
				new StringFieldAnalyser(),
				new SkillAnalyser(),
				new IdleResetAnalyser(),
				new GameMessageAnalyser(),
				new VariableAnalyser()
		);
		reflectionBuilder.add(
				new PlayerAnalyser()
		);
		return new ClientLoader(world, language, analyserBuilder.build(), reflectionBuilder.build());
	}

	public Applet load() throws Exception {
		String outputPath = System.getProperty("java.io.tmpdir") + "/.scapelog_cache";
		String url = "http://" + (world.isPresent() ? "world" + world.get().getId() + "." : "") + "runescape.com/l=" + language.getLanguage();
		String configUrl = url + "/jav_config.ws";

		print("Loading config...");
		JavConfig config = new JavConfig(configUrl);
		config.load();
		String gamePack = config.getConfig(JavConfig.GAMEPACK_NAME);
		String appletClass = config.getConfig(JavConfig.MAIN_CLASS);
		String codebase = config.getConfig(JavConfig.CODE_BASE);
		print("- Loaded");

		print("Downloading client...");
		Path jar;
		try {
			jar = WebUtils.download(codebase, gamePack, outputPath, gamePack.replace("/", ""));
		} catch (Exception e) {
			print("Failed to download the client, please try again");
			return null;
		}
		print("- Downloaded");

		print("Analysing client...");
		JarArchive archive;
		try {
			archive = analyse(jar, config);
		} catch (CryptographyException | IOException e) {
			print("Failed to analyse the client, retrying after redownloading");
			jar = WebUtils.download(codebase, gamePack, outputPath, gamePack.replace("/", ""));
			archive = analyse(jar, config);
		}
		print("- Analysed");

		boolean isDefault = !world.isPresent();
		print((isDefault ? "Received default" : "Using") + " world " + getWorldId(codebase) + ", lobby " + findLobbyId(config.getParameters()));

		if (archive == null) {
			print("Something went wrong with the client archive, please restart or report on forums");
			return null;
		}

		JarArchiveClassLoader classLoader = new JarArchiveClassLoader(archive);
		Applet applet = (Applet) classLoader.loadClass(appletClass.replace(".class", "")).newInstance();
		applet.setStub(new ClientAppletStub(config, new URL(codebase)));

		return applet;
	}

	private JarArchive analyse(Path file, JavConfig config) throws IOException, CryptographyException {
		GameClientModifier modifier = new GameClientModifier();
		GamePackArchives archives = modifier.unpack(config, file);

		// The gamepack's non-decrypted classes
		ClassNodeArchive nodeArchive = new ClassNodeArchive(archives.getGamepackArchive());
		nodeArchive.addClassNodes();
		Collection<ClassNode> classNodes = nodeArchive.toCollection();
		AnalysingOperation operation = new AnalysingOperation();
		analyse(classNodes, operation);

		// The decrypted classes
		nodeArchive = new ClassNodeArchive(archives.getClientArchive());
		nodeArchive.addClassNodes();
		classNodes = nodeArchive.toCollection();
		analyse(classNodes, operation);

		checkRequiredFields();

		// todo: temp
		/*if (ScapeLog.debug) {
			classNodes.forEach(ClassNodeUtils::dumpClass);
		}*/

		RSClassTransformer.addInjections(operation.getClassInjections());
		return archives.getGamepackArchive();
	}

	private void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		// run the injection analysers
		for (Analyser analyser : analysers) {
			analyser.analyse(classNodes, operation);
		}

		// run the reflection analysers
		for (ReflectionAnalyser analyser : reflectionAnalysers) {
			analyser.analyse(classNodes, operation);
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

	private void print(String message) {
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

	private int findLobbyId(Map<String, String> parameters) {
		for (String value : parameters.values()) {
			Matcher matcher = LOBBY_ID_PATTERN.matcher(value);
			if (matcher.find()) {
				String worldId = matcher.group(1);
				return Integer.parseInt(worldId);
			}
		}
		return -1;
	}

}