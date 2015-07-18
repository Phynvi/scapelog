package com.scapelog.client.plugins.impl;

import com.scapelog.api.ClientFeature;
import com.scapelog.api.plugin.OpenTechnique;
import com.scapelog.api.plugin.Plugin;
import com.scapelog.api.plugin.TabMode;
import com.scapelog.api.ui.Overlay;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

public final class AraxxorPlugin extends Plugin {

	public AraxxorPlugin() {
		super(TabMode.ON, AwesomeIcon.TH, "Araxxor", Optional.of("araxxor-plugin"), ClientFeature.OPENGL);
	}

	private final Overlay overlay = new Overlay(200, 20) {
		@Override
		public void paint(Graphics2D graphics) {
			graphics.setColor(Color.RED);
			graphics.fillRect(0, 0, overlay.getWidth(), overlay.getHeight());
			graphics.setColor(Color.WHITE);
			graphics.drawString("this will be araxxor hp bar lol", 10, 10);
		}
	};

	@Override
	public void onStart() {
		overlay.setX(10);
		overlay.setY(10);

		addOverlay(overlay);
	}

	@Override
	public void onStop() {
		super.onStop();

		removeOverlay(overlay);
	}

	@Override
	public Region getContent() {
		BorderPane content = new BorderPane();
		content.setCenter(new Label("Hi, this is Araxxor"));
		return content;
	}

	@Override
	public OpenTechnique getOpenTechnique() {
		return OpenTechnique.NONE;
	}

}