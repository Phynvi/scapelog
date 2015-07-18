package com.scapelog.client.ui;

import com.google.common.collect.Maps;
import com.scapelog.api.plugin.OpenTechnique;
import com.scapelog.api.plugin.Plugin;
import com.scapelog.api.ui.Overlay;
import com.scapelog.api.ui.TimedNotification;
import com.scapelog.api.ui.tab.BaseTab;
import com.scapelog.client.config.ClientConfigKeys;
import com.scapelog.client.config.Config;
import com.scapelog.client.config.UserInterfaceConfigKeys;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.client.event.ClientEventListener;
import com.scapelog.client.event.impl.ClientResizeEvent;
import com.scapelog.client.event.impl.PluginStartEvent;
import com.scapelog.client.model.WindowSizes;
import com.scapelog.client.plugins.PluginButton;
import com.scapelog.client.plugins.PluginLoader;
import com.scapelog.client.ui.component.AppletPanel;
import com.scapelog.client.ui.component.TitleBar;
import com.scapelog.client.ui.util.Fonts;
import com.scapelog.client.util.DiagnosticsLogger;
import com.sun.javafx.application.PlatformImpl;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;

import javax.swing.SwingUtilities;
import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class UserInterface {
	public static final String SECTION_NAME = "ui";

	private static final SimpleIntegerProperty borderRadius = new SimpleIntegerProperty(Config.getIntOrAdd(ClientConfigKeys.SECTION_NAME, ClientConfigKeys.BORDER_RADIUS, 2));

	private DecoratedFrame frame;
	private AppletPanel appletPanel;
	private FeaturesWindow features;

	private static final ObservableList<Overlay> activeOverlays = FXCollections.observableArrayList();

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

			final Scene scene = frame.getScene();
			Fonts.addDefaults(scene);

			setupResizeEvents();
			setupOutputCopying();

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
			scapeFrame.setVisible(true);
			scapeFrame.setMinimumSize(new Dimension(220, 200));
			scapeFrame.setBounds(frameX, frameY, frameWidth, frameHeight);
			if (frameMaximized) {
				scapeFrame.toggleMaximize();
			}
			WindowSizes.setFrame(scapeFrame);
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
		HBox staticContent = titleBar.getStaticContent();
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
					if (plugin.getOpenTechnique() == OpenTechnique.NONE) {
						return;
					}
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
		content.getChildren().addAll(buttonBox);
		staticContent.getChildren().addAll(featuresButton);
	}

	private void setupResizeEvents() {
		ClientEventDispatcher.registerListener(new ClientEventListener<ClientResizeEvent>(ClientResizeEvent.class) {
			@Override
			public void eventExecuted(ClientResizeEvent event) {
				int width = event.getWidth();
				int height = event.getHeight();
				SwingUtilities.invokeLater(() -> frame.getFrame().setSize(width, height));
			}
		});
	}

	private void setupOutputCopying() {
		Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
			if (e.getID() != KeyEvent.KEY_PRESSED) {
				KeyEvent keyEvent = (KeyEvent) e;

				if (keyEvent.isShiftDown() && keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_INSERT) {
					Platform.runLater(() -> {
						boolean added = false;

						String output = DiagnosticsLogger.getHashedOutput();
						if (output != null) {
							Map<DataFormat, Object> content = Maps.newHashMap();
							content.put(DataFormat.PLAIN_TEXT, output);
							added = Clipboard.getSystemClipboard().setContent(content);
						}
						new TimedNotification(added ? "ScapeLog's diagnostics have been copied to your clipboard!" : "Failed to copy diagnostics to clipboard!")
								.show(frame.getTitleBar().getContent(), 5, TimeUnit.SECONDS);
					});
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);
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

	public static void addOverlay(Overlay overlay) {
		addOverlays(overlay);
	}

	public static void addOverlays(Overlay... overlays) {
		activeOverlays.addAll(overlays);
	}

	public static ObservableList<Overlay> getOverlays() {
		return activeOverlays;
	}

	public static void removeOverlay(Overlay overlay) {
		activeOverlays.remove(overlay);
	}

	public static void removeOverlays(Overlay... overlays) {
		activeOverlays.removeAll(overlays);
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