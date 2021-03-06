package com.scapelog.client.plugins;

import com.scapelog.api.plugin.OpenTechnique;
import com.scapelog.api.plugin.Plugin;
import com.scapelog.api.util.Components;
import com.scapelog.client.ui.ScapeFrame;
import com.scapelog.client.ui.component.PopupWindow;
import com.scapelog.client.ui.component.TitleBar;
import de.jensd.fx.glyphs.GlyphIcons;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public final class PluginButton extends BorderPane {

	private final ScapeFrame scapeFrame;

	public PluginButton(Plugin plugin, ScapeFrame scapeFrame) {
		this.scapeFrame = scapeFrame;
		setId("plugin-button");
		Components.setPadding(this, 0, 6, 0, 6);
		setTranslateY(2);
		setMaxHeight(TitleBar.HEIGHT - 6.0);
		setPrefHeight(getMaxHeight());
		setMinHeight(getMaxHeight());

		Button button = new Button();
		setGraphic(button, plugin.buttonIconPropertyProperty().get());
		plugin.buttonIconPropertyProperty().addListener((observable, oldValue, newValue) -> setGraphic(button, newValue));

		setupContent(plugin, button);
	}

	private void setupContent(Plugin plugin, Button button) {
		OpenTechnique openTechnique = plugin.getOpenTechnique();
		Region content = plugin.getButtonContent();

		switch (openTechnique) {
			case EXPANDED_BUTTON:
				if (content != null) {
					content.setMaxWidth(100);
					content.setMinWidth(0);
					content.setVisible(false);
					button.setOnAction((e) -> {
						content.setVisible(!content.isVisible());
						setPrefWidth(button.getWidth() + (content.isVisible() ? content.getMaxWidth() : 0));
					});
					setCenter(content);
					setLeft(button);
					setPrefWidth(button.getWidth() + (content.isVisible() ? content.getMaxWidth() : 0));
					return;
				}
				break;
			case DRAWER:
				PopupWindow popOver = new PopupWindow(content, 300, 300);
				popOver.addFrameEvents(scapeFrame, button);
				popOver.setPrimary(true);

				button.setOnAction(e -> {
					if (popOver.isShowing()) {
						popOver.hide();
					} else {
						popOver.show(button, 0);
					}
				});
				break;
		}
		setCenter(button);

		double prefWidth = button.getWidth();
		if (openTechnique.equals(OpenTechnique.EXPANDED_BUTTON)) {
			prefWidth = button.getWidth() + (content != null && content.isVisible() ? content.getMaxWidth() : 0);
		}
		setPrefWidth(prefWidth);
	}

	private void setGraphic(Button button, GlyphIcons icon) {
		Label iconLabel = Components.createIconLabel(icon);
		button.setGraphic(iconLabel);
	}

}