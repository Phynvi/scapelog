package com.scapelog.client.ui;

import com.scapelog.api.plugin.Plugin;
import com.scapelog.client.ScapeLog;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.client.event.ClientEventListener;
import com.scapelog.client.event.impl.PluginStartEvent;
import com.scapelog.client.model.UserGroup;
import com.scapelog.client.plugins.PluginLoader;
import com.scapelog.client.ui.component.PopUp;
import com.scapelog.client.ui.component.tab.DashboardTab;
import com.scapelog.client.ui.component.tab.DeveloperTab;
import com.scapelog.client.ui.component.tab.NewsTab;
import com.scapelog.client.ui.component.tab.ReflectionTab;
import com.scapelog.client.ui.component.tab.SettingsTab;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;

public final class FeaturesWindow {

	private final PopUp popOver;
	private final ToggleButton trigger;

	public FeaturesWindow(ToggleButton trigger, ScapeFrame frame) {
		this.trigger = trigger;
		this.popOver = new PopUp(500, 400);
		this.popOver.setTitle("ScapeLog - features");
		this.popOver.addFrameEvents(frame, trigger);
	}

	public void setup(PluginLoader pluginLoader) {
		BorderPane pane = new BorderPane();
		pane.setId("features-window");

		TabPane tabs = new TabPane();
		tabs.setId("feature-tabs");
		tabs.setRotateGraphic(true);
		tabs.setSide(Side.LEFT);
		tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		tabs.setTabMinWidth(20);
		tabs.setTabMinHeight(30);

		tabs.setTabMaxWidth(30);
		tabs.setTabMaxHeight(30);

		tabs.getTabs().addAll(
				new DashboardTab().getTab(),
				new NewsTab().getTab(),
				new SettingsTab().getTab()
		);

		if (ScapeLog.debug || (ScapeLog.getUser() != null && ScapeLog.getUser().getGroups().contains(UserGroup.PLUGIN_DEVELOPER))) {
			tabs.getTabs().addAll(
					new DeveloperTab(pluginLoader).getTab(),
					new ReflectionTab().getTab()
			);
		}

		ClientEventDispatcher.registerListener(new ClientEventListener<PluginStartEvent>(PluginStartEvent.class) {
			@Override
			public void eventExecuted(PluginStartEvent event) {
				Platform.runLater(() -> {
					Plugin plugin = event.getPlugin();
					if (!plugin.hasTab()) {
						return;
					}
					Tab tab = plugin.getInitializedTab().getTab();
					if (tab == null || tabs.getTabs().contains(tab)) {
						return;
					}
					tabs.getTabs().add(tab);
					plugin.statusProperty().addListener((observable, oldStatus, newStatus) -> {
						if (!newStatus) {
							tabs.getTabs().remove(tab);
						}
					});
				});
			}
		});

		pane.setCenter(tabs);
		pane.setPrefSize(500, 400);
		// todo:
		popOver.setContent(pane);
		//popOver.setDragNode(tabs);
	}

	public void toggle() {
		if (popOver.isShowing()) {
			popOver.hide();
			return;
		}
		popOver.show(trigger, 0);
	}

	public boolean isVisible() {
		return popOver.isShowing();
	}

	public SimpleBooleanProperty getVisibilityProperty() {
		return popOver.getVisibilityProperty();
	}

}