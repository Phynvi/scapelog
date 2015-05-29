package com.scapelog.client.ui.component.tab;

import com.google.common.collect.Lists;
import com.scapelog.api.ClientFeature;
import com.scapelog.api.event.Event;
import com.scapelog.api.event.EventListener;
import com.scapelog.api.event.impl.GameMessageEvent;
import com.scapelog.api.event.impl.SkillEvent;
import com.scapelog.api.event.impl.VariableEvent;
import com.scapelog.api.model.SkillSet;
import com.scapelog.api.plugin.Plugin;
import com.scapelog.api.ui.tab.IconTab;
import com.scapelog.api.util.Components;
import com.scapelog.api.util.SettingsUtils;
import com.scapelog.client.ScapeLog;
import com.scapelog.client.event.EventDispatcher;
import com.scapelog.client.plugins.PluginLoader;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

public final class DeveloperTab extends IconTab {

	private final PluginLoader pluginLoader;
	private final ObservableList<Plugin> startedPlugins = FXCollections.observableArrayList();

	private final List<EventMessage> events = Lists.newArrayList();
	private final List<Class<?>> hiddenEventTypes = Lists.newArrayList();
	private final List<Class<?>> blockedEventTypes = Lists.newArrayList();

	private TextArea textArea;
	private ListView<EventMessage> eventList;
	private ScrollPane scrollPane;

	public DeveloperTab(PluginLoader pluginLoader) {
		super(AwesomeIcon.TERMINAL, "Developer tab");
		this.pluginLoader = pluginLoader;
	}

	@Override
	public Node getTabContent() {
		VBox topContent = new VBox(10);
		topContent.setPadding(new Insets(10, 10, 10, 10));

		textArea = new TextArea();
		print("---> Plugin test environment\n");

		Button addButton = Components.createBorderedButton("Browse...");
		addButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jar", "*.jar"));
			File plugin = fileChooser.showOpenDialog(null);
			if (plugin == null || plugin.isDirectory()) {
				return;
			}
			print("Attempting to run " + plugin.getAbsolutePath());
			ScapeLog.getExecutor().submit(() -> {
				try {
					Plugin parsedPlugin = pluginLoader.parsePlugin(plugin);
					boolean started = pluginLoader.startPlugin(parsedPlugin);
					if (started) {
						print("Successfully started plugin '" + parsedPlugin.getName() + "'!\n");
						startedPlugins.add(parsedPlugin);
					} else {
						print("Couldn't start plugin, all necessary features are not enabled");
					}
				} catch (Exception ex) {
					print("Failed to run plugin: " + ex);
					ex.printStackTrace();
				}
			});
		});

		Button stopButton = Components.createBorderedButton("Stop");
		stopButton.setOnAction(e -> {
			int runningPlugins = startedPlugins.size();
			if (runningPlugins > 1) {
				print("Stopped " + runningPlugins + " plugins:");
			}
			for (Plugin plugin : startedPlugins) {
				plugin.stop();
				if (runningPlugins == 1) {
					print("Stopped " + runningPlugins + " plugin '" + plugin.getName() + "'");
				} else if (runningPlugins > 1) {
					print("    " + plugin.getName());
				}
			}
			startedPlugins.clear();
		});

		topContent.getChildren().addAll(
				Components.createBox("Run plugins", addButton),
				Components.createBox("Stop plugins", stopButton)
		);

		ScrollPane scrollPane = new ScrollPane(textArea);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		BorderPane content = new BorderPane();
		content.setTop(topContent);
		content.setCenter(scrollPane);

		Tab pluginTab = new Tab("Plugins");
		pluginTab.setContent(content);

		Tab eventTab = new Tab("Events");
		eventTab.setContent(getEventTab());

		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().addAll(
				pluginTab,
				eventTab
		);
		return tabPane;
	}

	private Parent getEventTab() {
		VBox topContent = new VBox(10);
		topContent.setPadding(new Insets(10, 10, 10, 10));

		eventList = new ListView<>();

		registerFeature(topContent, ClientFeature.SKILLS, new EventListener<SkillEvent>(SkillEvent.class) {
			@Override
			public void eventExecuted(SkillEvent event) {
				printEvent(new EventMessage(event), false);
			}
		});
		registerFeature(topContent, ClientFeature.GAME_MESSAGES, new EventListener<GameMessageEvent>(GameMessageEvent.class) {
			@Override
			public void eventExecuted(GameMessageEvent event) {
				printEvent(new EventMessage(event), false);
			}
		});
		registerFeature(topContent, ClientFeature.VARIABLES, new EventListener<VariableEvent>(VariableEvent.class) {
			@Override
			public void eventExecuted(VariableEvent event) {
				printEvent(new EventMessage(event), false);
			}
		});

		scrollPane = new ScrollPane(eventList);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		BorderPane content = new BorderPane();
		content.setTop(topContent);
		content.setCenter(scrollPane);
		return content;
	}

	private <E extends Event> void registerFeature(VBox topContent, ClientFeature feature, EventListener<E> eventListener) {
		Class<?> type = feature.getEventParser().getType();
		Button clearButton = Components.createBorderedButton("Clear");
		clearButton.setOnAction(e -> clearEvents(type));

		topContent.getChildren().add(Components.createBox(
				feature.getName(),
				clearButton,
				SettingsUtils.createCheckBoxSetting("Block", selected -> toggleBlock(type, selected), false),
				SettingsUtils.createCheckBoxSetting("Show", selected -> showEvents(type, selected), true)
		));
		EventDispatcher.registerListener(eventListener);
	}

	private boolean isBlocked(Class<?> type) {
		return blockedEventTypes.contains(type);
	}

	private boolean isHidden(Class<?> type) {
		return hiddenEventTypes.contains(type);
	}

	private void clearEvents(Class<?> type) {
		ListIterator<EventMessage> iterator = events.listIterator();
		while (iterator.hasNext()) {
			EventMessage message = iterator.next();
			if (message.event.getClass().equals(type)) {
				iterator.remove();
			}
		}
		showEvents(type, !isHidden(type));
	}

	private void toggleBlock(Class<?> type, boolean block) {
		if (block) {
			blockedEventTypes.add(type);
		} else {
			blockedEventTypes.remove(type);
		}
		showEvents(type, !isHidden(type));
	}

	private void showEvents(Class<?> type, boolean show) {
		if (show) {
			hiddenEventTypes.remove(type);
		} else {
			hiddenEventTypes.add(type);
		}
		eventList.getItems().clear();
		for (EventMessage event : events) {
			if (isHidden(event.event.getClass()) || isBlocked(event.event.getClass())) {
				continue;
			}
			printEvent(event, true);
		}
		scrollPane.setVvalue(1.0);
	}

	private void printEvent(EventMessage event, boolean reload) {
		if (isBlocked(event.event.getClass())) {
			return;
		}
		String str = event.message;
		if (str == null) {
			str = getString(event);
		}
		if (str == null) {
			return;
		}
		event.message = str;
		if (!reload) {
			events.add(event);
		}
		Platform.runLater(() -> eventList.getItems().add(event));
	}

	private String getString(EventMessage event) {
		Event evt = event.event;
		if (evt.getClass().equals(SkillEvent.class)) {
			SkillEvent e = (SkillEvent) evt;
			return "[skill] skill=" + SkillSet.getName(e.getSkill().getId()) + ", levelChange=" + e.getLevelChange() + ", xpChange=" + e.getXpChange();
		}
		if (evt.getClass().equals(GameMessageEvent.class)) {
			GameMessageEvent e = (GameMessageEvent) evt;
			return "[message] message=" + e.getMessage() + ", type=" + e.getType();
		}
		if (evt.getClass().equals(VariableEvent.class)) {
			VariableEvent e = (VariableEvent) evt;
			return "[setting] id=" + e.getId() + ", oldValue=" + e.getOldValue() + ", newValue=" + e.getNewValue();
		}
		return null;
	}

	private void print(String message) {
		textArea.appendText(message + "\n");
	}

	static class EventMessage {

		private final Event event;
		private String message;

		public EventMessage(Event event) {
			this.event = event;
		}

		@Override
		public String toString() {
			return message;
		}
	}

}