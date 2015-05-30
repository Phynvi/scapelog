package com.scapelog.client.ui;

import com.scapelog.api.plugin.OpenTechnique;
import com.scapelog.api.plugin.Plugin;
import com.scapelog.api.ui.tab.BaseTab;
import com.scapelog.client.config.Config;
import com.scapelog.client.config.UserInterfaceConfigKeys;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.client.event.ClientEventListener;
import com.scapelog.client.event.impl.PluginStartEvent;
import com.scapelog.client.plugins.PluginButton;
import com.scapelog.client.plugins.PluginLoader;
import com.scapelog.client.ui.component.AppletPanel;
import com.scapelog.client.ui.component.TitleBar;
import com.scapelog.client.ui.util.Fonts;
import com.sun.javafx.application.PlatformImpl;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import javax.swing.SwingUtilities;
import java.applet.Applet;

public final class UserInterface {
	public static final String SECTION_NAME = "ui";

	private static final SimpleIntegerProperty borderRadius = new SimpleIntegerProperty(2/*Config.getIntOrAdd(ClientConfigKeys.SECTION_NAME, ClientConfigKeys.BORDER_RADIUS, 2)*/);

	private DecoratedFrame frame;
	private AppletPanel appletPanel;
	private FeaturesWindow features;

	public void setup(final PluginLoader pluginLoader) {
		PlatformImpl.startup(() -> {
			appletPanel = new AppletPanel();

			int defaultWidth = 1000;
			int defaultHeight = 700;
			int frameX = Config.getIntOrAdd(SECTION_NAME, UserInterfaceConfigKeys.X, 0);
			int frameY = Config.getIntOrAdd(SECTION_NAME, UserInterfaceConfigKeys.Y, 0);
			int frameWidth = Config.getIntOrAdd(SECTION_NAME, UserInterfaceConfigKeys.WIDTH, defaultWidth);
			int frameHeight = Config.getIntOrAdd(SECTION_NAME, UserInterfaceConfigKeys.HEIGHT, defaultHeight);
			boolean frameMaximized = Config.getBooleanOrAdd(SECTION_NAME, UserInterfaceConfigKeys.MAXIMIZED, false);
			int width = frameMaximized ? defaultWidth : frameWidth;
			int height = frameMaximized ? defaultWidth : frameHeight;

			frame = new DecoratedFrame("ScapeLog", width, height, TitleBar.HEIGHT, true) {
				@Override
				protected void setContent(Scene scene, int width, int sceneHeight) {
					getFrame().getContentPane().add(appletPanel);
					super.setContent(scene, width, sceneHeight);
				}
			};

			Scene scene = frame.getScene();
			Fonts.addDefaults(scene);

			scene.widthProperty().addListener((observable, oldValue, newValue) -> {
				double newWidth = (double) newValue;
				resizeApplet((int) newWidth, (int) scene.getHeight());
			});
			scene.heightProperty().addListener((observable, oldValue, newValue) -> {
				double newHeight = (double) newValue;
				resizeApplet((int) scene.getWidth(), (int) newHeight);
			});

			borderRadius.addListener((observable, oldValue, newValue) -> resizeApplet((int) scene.getWidth(), (int) scene.getHeight()));

			setupTitleBar(frame.getTitleBar(), pluginLoader);

			appletPanel.setBounds(2, TitleBar.HEIGHT, (int) scene.getWidth() - (UserInterface.getBorderRadius() * 2), (int) scene.getHeight());

			ScapeFrame scapeFrame = frame.getFrame();
			SwingUtilities.invokeLater(() -> {
				scapeFrame.setBounds(frameX, frameY, frameWidth, frameHeight);
				scapeFrame.setVisible(true);
				if (frameMaximized) {
					scapeFrame.toggleMaximize();
				}
			});
		});
	}

	private void resizeApplet(int width, int height) {
		int borderSize = UserInterface.getBorderRadius();
		int newWidth = width - (borderSize * 2) - 2;
		int newHeight = height - TitleBar.HEIGHT - borderSize - 1;
		appletPanel.setBounds(borderSize + 1, TitleBar.HEIGHT, newWidth, newHeight);
		appletPanel.revalidate();
		appletPanel.repaint();
	}

	public void setupTitleBar(TitleBar titleBar, PluginLoader pluginLoader) {
		HBox content = titleBar.getContent();

/*		Button reloadButton = AwesomeDude.createIconButton(AwesomeIcon.REPEAT);
		HBox.setMargin(reloadButton, new Insets(3, 0, 0, 6));
		reloadButton.setId("reload");
		reloadButton.setTooltip(new Tooltip("Reload client"));
		reloadButton.setOnAction((e) -> {
			//Action response = Dialogs.create().title("Reload?").message("Reload client? Make sure you are safe in-game!").showConfirm();
			//if (response == Dialog.Actions.YES) {
				ClientEventDispatcher.fireEvent(new ClientReloadEvent());
			//}
		});
*/
		ToggleButton featuresButton = new ToggleButton();
		AwesomeDude.setIcon(featuresButton, AwesomeIcon.BARS);
		featuresButton.setId("features");
		HBox.setMargin(featuresButton, new Insets(2, 8, 0, 4));
		featuresButton.setTooltip(new Tooltip("Features"));
		featuresButton.setOnAction((e) -> {
			features.toggle();
			featuresButton.setSelected(features.isVisible());
		});
		features = new FeaturesWindow(featuresButton, frame.getFrame());
		features.setup(pluginLoader);
		features.getVisibilityProperty().addListener((observable, oldValue, newValue) -> {
			featuresButton.setSelected(newValue);
		});

		HBox buttonBox = new HBox(3.0);
		ClientEventDispatcher.registerListener(new ClientEventListener<PluginStartEvent>(PluginStartEvent.class) {
			@Override
			public void eventExecuted(PluginStartEvent event) {
				Platform.runLater(() -> {
					Plugin plugin = event.getPlugin();
					if (plugin == null) {
						return;
					}
					PluginButton button = new PluginButton(plugin, frame.getFrame());
					BaseTab tab = plugin.getInitializedTab();
					if (tab == null) {
						return;
					}
					OpenTechnique openTechnique = plugin.getOpenTechnique();

					tab.visibilityPropertyProperty().addListener((observable, oldValue, newValue) -> {
						boolean visible = newValue;
						if (visible && !buttonBox.getChildren().contains(button)) {
							if (openTechnique.equals(OpenTechnique.EXPANDED_BUTTON)) {
								buttonBox.getChildren().add(0, button);
							} else if (openTechnique.equals(OpenTechnique.DRAWER)) {
								buttonBox.getChildren().add(button);
							}
						} else {
							buttonBox.getChildren().remove(button);
						}
					});

					if (tab.visibilityPropertyProperty().get()) {
						if (openTechnique.equals(OpenTechnique.EXPANDED_BUTTON)) {
							buttonBox.getChildren().add(0, button);
						} else if (openTechnique.equals(OpenTechnique.DRAWER)) {
							buttonBox.getChildren().add(button);
						}
					}

					plugin.statusProperty().addListener((observable, oldStatus, newStatus) -> {
						if (!plugin.isRunning()) {
							buttonBox.getChildren().remove(button);
						}
					});
				});
			}
		});
		content.getChildren().addAll(buttonBox, /*reloadButton, */featuresButton);
	}

	public void saveSize() {
		try {
			Config.runBatch(() -> {
				Config.setBoolean(SECTION_NAME, UserInterfaceConfigKeys.MAXIMIZED, frame.getFrame().isMaximized());
				if (!frame.getFrame().isMaximized()) {
					Scene scene = frame.getScene();
					Config.setInt(SECTION_NAME, UserInterfaceConfigKeys.X, frame.getFrame().getX());
					Config.setInt(SECTION_NAME, UserInterfaceConfigKeys.Y, frame.getFrame().getY());
					Config.setInt(SECTION_NAME, UserInterfaceConfigKeys.WIDTH, (int) scene.getWidth());
					Config.setInt(SECTION_NAME, UserInterfaceConfigKeys.HEIGHT, (int) scene.getHeight());
				}
				return null;
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addApplet(Applet applet) {
		appletPanel.setApplet(applet);
	}

	public void removeApplet() {
		appletPanel.removeApplet();
	}

	public static int getBorderRadius() {
		return borderRadius.get();
	}

	public static SimpleIntegerProperty borderRadiusProperty() {
		return borderRadius;
	}

}