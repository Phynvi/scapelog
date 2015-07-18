package com.scapelog.client.plugins;

import com.scapelog.api.ClientFeature;
import com.scapelog.api.plugin.Plugin;
import com.scapelog.client.ClientFeatures;
import com.scapelog.client.ScapeLog;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.client.event.ClientEventListener;
import com.scapelog.client.event.impl.FeatureEnableEvent;
import com.scapelog.client.event.impl.PluginStartEvent;
import com.scapelog.client.loader.archive.ClassNodeArchive;
import com.scapelog.client.loader.archive.JarArchive;
import com.scapelog.client.loader.archive.JarArchiveClassLoader;
import com.scapelog.client.loader.codec.JarArchiveCodec;
import com.scapelog.client.plugins.impl.HighscoresPlugin;
import com.scapelog.client.plugins.impl.SkillTrackerPlugin;
import com.scapelog.client.plugins.impl.SlayerPlugin;
import com.scapelog.client.plugins.impl.TimerPlugin;
import com.scapelog.client.util.Debug;
import javafx.application.Platform;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public final class PluginLoader {

	private List<Plugin> plugins = new ArrayList<>();

	public PluginLoader() {

	}

	public void start() {
		ClientEventDispatcher.registerListener(new ClientEventListener<FeatureEnableEvent>(FeatureEnableEvent.class) {
			@Override
			public void eventExecuted(FeatureEnableEvent event) {
				startPlugins();
			}
		});
		startPlugins();
	}

	public void load() {
		plugins.add(new SkillTrackerPlugin());
		plugins.add(new SlayerPlugin());
		plugins.add(new HighscoresPlugin());
		plugins.add(new TimerPlugin());
		//plugins.add(new AraxxorPlugin());
	}

	public void startPlugins() {
		plugins.forEach(this::startPlugin);
	}

	public boolean startPlugin(Plugin plugin) {
		if (plugin.isStarted()) {
			return false;
		}
		boolean canStart = true;
		for (ClientFeature feature : plugin.getDependingFeatures()) {
			if (ScapeLog.debug) {
				break;
			}
			if (!ClientFeatures.isEnabled(feature)) {
				canStart = false;
			}
		}
		if (!canStart) {
			Debug.println("Unable to start plugin '%s', not all required features are enabled", plugin.getName());
			return false;
		}
		try {
			plugin.start();
			Debug.println("Plugin started: %s", plugin.getName());
			Platform.runLater(() -> ClientEventDispatcher.fireEvent(new PluginStartEvent(plugin)));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Plugin parsePlugin(File file) throws Exception {
		if (file == null || !file.exists() || file.isDirectory() || !file.getName().endsWith(".jar")) {
			return null;
		}
		JarArchiveCodec jarArchiveCodec = new JarArchiveCodec();
		JarArchive archive;
		try (FileInputStream inputStream = new FileInputStream(file)) {
			archive = jarArchiveCodec.read(inputStream);
		}
		JarArchiveClassLoader classLoader = JarArchiveClassLoader.create(archive);
		ClassNodeArchive nodeArchive = new ClassNodeArchive(archive);
		nodeArchive.addClassNodes();

		Plugin plugin = null;
		for (String name : archive.getArchivedData().keySet()) {
			ClassNode node = archive.toClassNode(name);
			if (node == null || node.superName == null) {
				continue;
			}
			boolean isPlugin = node.superName.equals(Type.getInternalName(Plugin.class));
			if (!isPlugin) {
				continue;
			}

			Class<?> clazz = classLoader.nodeToClass(node);
			plugin = (Plugin) clazz.newInstance();
			break;
		}
		return plugin;
	}

}