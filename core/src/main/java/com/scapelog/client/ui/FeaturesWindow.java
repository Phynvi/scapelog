package com.scapelog.client.ui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.lang.reflect.Field;

import com.scapelog.api.plugin.Plugin;
import com.scapelog.api.util.Components;
import com.scapelog.client.ScapeLog;
import com.scapelog.client.config.ClientConfigKeys;
import com.scapelog.client.config.Config;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.client.event.ClientEventListener;
import com.scapelog.client.event.impl.PluginStartEvent;
import com.scapelog.client.plugins.PluginLoader;
import com.scapelog.client.ui.component.PopupWindow;
import com.scapelog.client.ui.component.tab.DashboardTab;
import com.scapelog.client.ui.component.tab.DeveloperTab;
import com.scapelog.client.ui.component.tab.NewsTab;
import com.scapelog.client.ui.component.tab.NotificationTab;
import com.scapelog.client.ui.component.tab.ReflectionTab;
import com.scapelog.client.ui.component.tab.SettingsTab;
import com.scapelog.util.proguard.Keep;
import com.sun.javafx.scene.control.skin.TabPaneSkin;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public final class FeaturesWindow {

	private final Runnable focusLossEvent;

	private final PopupWindow popup;
	private final ToggleButton trigger;
	private final DecoratedFrame frame;

	public FeaturesWindow(ToggleButton trigger, DecoratedFrame frame) {
		this.trigger = trigger;
		this.frame = frame;
		this.popup = new PopupWindow(500, 400);
		this.popup.setTitle("Features");
		this.popup.setPrimary(true);
		this.popup.addFrameEvents(frame.getFrame(), trigger);

		this.focusLossEvent = () -> {
			boolean close = Config.getBooleanOrAdd(ClientConfigKeys.SECTION_NAME, ClientConfigKeys.FOCUS_LOSS_CLOSE, false);
			if (close && !popup.isDetached() && !popup.isFocused() && !frame.getFrame().isFocused()) {
				popup.hide();
			}
		};
		frame.getFrame().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				focusLossEvent.run();
			}
		});
		popup.focusedProperty().addListener((observable, wasFocused, focused) -> {
			if (!focused) {
				focusLossEvent.run();
			}
		});
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
				new SettingsTab().getTab(),
				new NotificationTab(frame.getTitleBar()).getTab(),
				new NewsTab().getTab()
		);

		tabs.skinProperty().addListener((observable, oldSkin, newSkin) -> {
			modifySkin(newSkin);
		});

		// Developer tab would've been visible for users with developer rank but since that's not happening
		// we'll limit it to debug mode
		if (ScapeLog.debug) {
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
		popup.setContent(pane);
	}

	public void toggle() {
		if (popup.isShowing()) {
			popup.hide();
			popup.setDetached(false);
			return;
		}
		popup.show(trigger, 0);
	}

	public boolean isVisible() {
		return popup.isShowing();
	}

	public SimpleBooleanProperty getVisibilityProperty() {
		return popup.getVisibilityProperty();
	}

	@Keep
	private void modifySkin(Skin<?> newSkin) {
		if (newSkin.getClass().equals(TabPaneSkin.class)) {
			TabPaneSkin skin = (TabPaneSkin) newSkin;
			for (Node child : skin.getChildren()) {
				if (!child.getStyleClass().contains("tab-header-area")) {
					continue;
				}
				StackPane stackPane = (StackPane) child;
				popup.setDraggable(stackPane);
				try {
					Field controlButtons = stackPane.getClass().getDeclaredField("controlButtons");
					boolean accessible = controlButtons.isAccessible();
					controlButtons.setAccessible(true);

					StackPane controlButtonsPane = (StackPane) controlButtons.get(stackPane);
					controlButtons.setAccessible(accessible);

					controlButtonsPane.setVisible(true);
					controlButtonsPane.setPrefHeight(33);
					controlButtonsPane.setPrefWidth(30);
					controlButtonsPane.getChildren().clear();

					final Button iconButton = Components.createIconButton(FontAwesomeIcon.THUMB_TACK, "15.0");
					iconButton.setTooltip(new Tooltip("Detach"));
					iconButton.setStyle("-fx-rotate: 135;");
					controlButtonsPane.getChildren().add(iconButton);
					iconButton.setLayoutX(10);
					iconButton.setLayoutY(15);
					iconButton.setOnAction(e -> popup.toggleDetach());

					popup.detachedProperty().addListener((observable1, oldVal, newVal) -> {
						Tooltip tooltip = iconButton.getTooltip();
						tooltip.setText(newVal ? "Attach" : "Detach");
						iconButton.setStyle("-fx-rotate: " + (newVal ? "90" : "135"));
					});
				} catch (NoSuchFieldException | IllegalAccessException e) {
					System.err.println("Failed to create detach button");
				}
			}
		}
	}

}