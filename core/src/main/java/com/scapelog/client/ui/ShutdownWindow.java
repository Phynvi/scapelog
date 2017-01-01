package com.scapelog.client.ui;

import com.scapelog.api.ui.Utils;
import com.scapelog.api.util.Components;
import com.scapelog.client.ui.component.TitleBar;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public final class ShutdownWindow {
	private static final Stage popupWindow;

	private static ScapeFrame scapeFrame;

	static {
		popupWindow = createWindow();
	}

	private static Stage createWindow() {
		BorderPane pane = new BorderPane();
		Components.setPadding(pane, 0, 1, 0, 1);

		TextArea textArea = new TextArea();
		textArea.setWrapText(true);
		textArea.setEditable(false);
		textArea.setText("The time of ScapeLog is coming to an end. Jagex can now detect ScapeLog, if it has not been able to before.\n" +
				"I've received a 48 hour ban for using an illegal third-party client and ScapeLog being the only client I use to play it's clear what caused it.\n\n" +
				"I've decided to completely shut down ScapeLog on January 1st to hopefully prevent you from sharing the same fate.\n" +
				"As of now ScapeLog is no longer safe to use and I advice you to stop using it immediately.\n\n" +
				"For those interested in the code, most of it is available at https://github.com/cubeee/scapelog\n\n" +
				"Thank you for using ScapeLog");

		pane.setCenter(textArea);

		ScapeStage scapeStage = Utils.createStage(pane, 600, 450);
		Stage stage = scapeStage.getStage();
		stage.setTitle("Shutdown notification");

		HBox titleBarContent = scapeStage.getTitleBar().getContent();

		Label messageLabel = new Label();
		messageLabel.setPrefHeight(TitleBar.HEIGHT - 2);
		titleBarContent.getChildren().add(messageLabel);
		return stage;
	}

	public static void show() {
		Platform.runLater(() -> {
			popupWindow.show();
			Utils.centerToScreen(popupWindow);
		});
	}

	public static void setFrame(ScapeFrame scapeFrame) {
		if (ShutdownWindow.scapeFrame != null) {
			return;
		}
		ShutdownWindow.scapeFrame = scapeFrame;
	}
}