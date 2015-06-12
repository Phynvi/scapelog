package com.scapelog.client.ui.component;

import com.scapelog.api.util.Components;
import com.scapelog.client.ui.ScapeFrame;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.swing.WindowConstants;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

public final class WindowControls extends HBox {
	Button minimizeButton = new Button("_");
	Button maximizeButton = AwesomeDude.createIconButton(AwesomeIcon.EXPAND);
	Button closeButton = new Button("x");

	public WindowControls() {
		setId("window-controls");
		setAlignment(Pos.CENTER);
		getStyleClass().add("controls");

		minimizeButton.setId("minimize");
		Components.setWebFont(minimizeButton, "Nunito");
		HBox.setMargin(minimizeButton, new Insets(0, 0, 0, 0));
		getChildren().add(minimizeButton);

		maximizeButton.setId("maximize");
		HBox.setMargin(maximizeButton, new Insets(0, 0, 0, 0));

		closeButton.setId("close");
		Components.setWebFont(closeButton, "Nunito");
		HBox.setMargin(closeButton, new Insets(0, 0, 0, 0));
		getChildren().add(closeButton);

		ScapeFrame.MAXIMIZED_PROPERTY.addListener((observable, oldValue, newValue) -> {
			sizeChanged(newValue);
		});
	}

	public WindowControls(ScapeFrame frame) {
		this();
		minimizeButton.setOnAction((e) -> frame.setState(frame.getState() | ScapeFrame.ICONIFIED));
		maximizeButton.setOnAction((e) -> frame.toggleMaximize());
		closeButton.setOnAction((e) -> {
			switch(frame.getDefaultCloseOperation()) {
				case WindowConstants.DISPOSE_ON_CLOSE:
					frame.dispose();
					break;
				case WindowConstants.HIDE_ON_CLOSE:
					frame.setVisible(false);
					break;
				case WindowConstants.EXIT_ON_CLOSE:
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					break;
			}
		});

		if (frame.isResizable() && Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH)) {
			getChildren().add(1, maximizeButton);
		}
	}

	public WindowControls(Stage stage) {
		this();
		minimizeButton.setOnAction((e) -> stage.setIconified(true));
		maximizeButton.setOnAction((e) -> stage.setMaximized(!stage.isMaximized()));
		closeButton.setOnAction((e) -> stage.close());
	}

	public void sizeChanged(boolean maximized) {
		Platform.runLater(() -> {
			Label label = AwesomeDude.createIconLabel(maximized ? AwesomeIcon.COMPRESS : AwesomeIcon.EXPAND);
			maximizeButton.setGraphic(label);
		});
	}

}