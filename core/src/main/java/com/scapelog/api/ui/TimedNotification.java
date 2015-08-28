package com.scapelog.api.ui;

import com.scapelog.api.util.Components;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.concurrent.TimeUnit;

public final class TimedNotification extends Popup {

	public TimedNotification(String text) {
		this(text, 10.0, 10.0);
	}

	public TimedNotification(String text, double x, double y) {
		setX(x);
		setY(y);

		FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, 10.0, 10.0);
		Components.setPadding(flowPane, 15);

		Label label = new Label(text);
		label.setWrapText(true);
		flowPane.getChildren().addAll(label);

		getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> hide());

		getContent().add(flowPane);
	}

	public void show(Node node, long duration, TimeUnit timeUnit) {
		show(node, getX(), getY());

		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(timeUnit.toMillis(duration)), e -> {
			hide();
		}));
		timeline.play();
	}

}