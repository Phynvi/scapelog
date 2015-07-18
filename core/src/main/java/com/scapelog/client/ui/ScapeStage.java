package com.scapelog.client.ui;

import com.scapelog.client.ui.component.TitleBar;
import javafx.scene.Parent;
import javafx.stage.Stage;

public final class ScapeStage {

	private final Stage stage;

	private final Parent contentPane;

	private final TitleBar titleBar;

	public ScapeStage(Stage stage, Parent contentPane, TitleBar titleBar) {
		this.stage = stage;
		this.contentPane = contentPane;
		this.titleBar = titleBar;
	}

	public Stage getStage() {
		return stage;
	}

	public Parent getContentPane() {
		return contentPane;
	}

	public TitleBar getTitleBar() {
		return titleBar;
	}

}