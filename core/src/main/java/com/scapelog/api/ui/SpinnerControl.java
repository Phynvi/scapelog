package com.scapelog.api.ui;

import com.google.common.collect.Lists;
import com.scapelog.api.util.Components;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;

public class SpinnerControl extends HBox {
 
	private TextField textField;
	private Button buttonIncrease;
	private Button buttonDecrease;
	
	private double incrementationValue = 1.0;
	private double minValue = 0.0;
	private double maxValue = 100.0;
	
	private int decimalsPlaces = 2;

	private List<ChangeListener<Double>> changeListeners = Lists.newArrayList();
	
	public SpinnerControl() {
		super();
		getStyleClass().add("spinner-control");

		textField = new TextField();
		textField.setPrefWidth(155);
		textField.getStyleClass().add("textfield");
		textField.setAlignment(Pos.BASELINE_LEFT);

		buttonIncrease = Components.createBorderedButton("+");
		buttonIncrease.getStyleClass().add("increase");
		addPressAndHoldHandler(buttonIncrease, Duration.millis(100), event -> {
			double value = getValue();
			double newValue = maxValue;
			if (value < maxValue) {
				newValue = getValue() + incrementationValue;
			}
			setValue(newValue);
		});

		buttonDecrease = Components.createBorderedButton("-");
		buttonDecrease.getStyleClass().add("decrease");
		addPressAndHoldHandler(buttonDecrease, Duration.millis(100), event -> {
			double value = getValue();
			double newValue = minValue;
			if (value > minValue) {
				newValue = getValue() - incrementationValue;
			} else if (value >= maxValue) {
				newValue = maxValue;
			}
			setValue(newValue);
		});

		textField.heightProperty().addListener((observable, oldValue, newValue) -> {
			double buttonHeight = newValue.doubleValue() / 2;
			buttonDecrease.setMinHeight(buttonHeight);
			buttonIncrease.setMinHeight(buttonHeight);
		});
		
		VBox vBoxButtons = new VBox();
		vBoxButtons.getChildren().addAll(buttonIncrease, buttonDecrease);
		
		HBox.setHgrow(textField, Priority.ALWAYS);

		getChildren().addAll(textField, vBoxButtons);
	}
	
	public void setIncrementationValue(double value) {
		incrementationValue = value;
	}
	
	public double getIncrementationValue() {
		return incrementationValue;
	}
	
	public void setMinValue(double value) {
		minValue = value;
	}
	
	public double getMinValue() {
		return minValue;
	}
	
	public void setMaxValue(double value) {
		maxValue = value;
	}
	
	public double getMaxValue() {
		return maxValue;
	}
	
	public void setDecimalsPlaces(int value) {
		decimalsPlaces = value;
	}
	
	public int getDecimalsPlaces() {
		return decimalsPlaces;
	}
	
	public void setValue(double value) {
		double oldValue = value;
		try {
			oldValue = getValue();
		} catch (NumberFormatException e) {
			/**/
		}

		String stringValue = String.format("%." + String.valueOf(decimalsPlaces) + "f", value).replace(",", ".");
		textField.setText(stringValue);

		final double oldVal = oldValue;
		changeListeners.stream().filter(changeListener -> changeListener != null).forEach(changeListener -> changeListener.changed(null, oldVal, value));
	}
	
	public double getValue() {
		return Double.parseDouble(textField.getText().replace(",", "."));
	}
	
	public void setSpinnerEditable(boolean value) {
		textField.setEditable(value);
	}
	
	public boolean isSpinnerEditable() {
		return textField.isEditable();
	}
	
	public void setSpinnerPrefSize(double width, double height) {
		this.setPrefSize(width, height);
	}
	
	public void setSpinnerMinSize(double width, double height) {
		this.setMinSize(width, height);
	}
	
	public void setSpinnerMaxSize(double width, double height) {
		this.setMaxSize(width, height);
	}
	
	public void setSpinnerPrefWidth(double value) {
		this.setPrefWidth(value);
	}
	
	public void setSpinnerMinWidth(double value) {
		this.setMinWidth(value);
	}
	
	public void setSpinnerMaxWidth(double value) {
		this.setMaxWidth(value);
	}
	
	public void setSpinnerPrefHeight(double value) {
		this.setPrefHeight(value);
	}
	
	public void setSpinnerMinHeight(double value) {
		this.setMinHeight(value);
	}
	
	public void setSpinnerMaxHeight(double value) {
		this.setMaxHeight(value);
	}
	
	public void setOnActionButtonIncrease(EventHandler<ActionEvent> event) {
		buttonIncrease.setOnAction(event);
	}
	
	public void setOnActionButtonDecrease(EventHandler<ActionEvent> event) {
		buttonDecrease.setOnAction(event);
	}
	
	public void setTextField(TextField value) {
		textField = value;
	}
	
	public TextField getTextField() {
		return textField;
	}

	public void addChangeListener(ChangeListener<Double> changeListener) {
		changeListeners.add(changeListener);
	}

	private void addPressAndHoldHandler(Node node, Duration duration, EventHandler<MouseEvent> handler) {
		class Wrapper<T> {
			T content;
		}
		Wrapper<MouseEvent> eventWrapper = new Wrapper<>();

		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0), event -> {
			handler.handle(eventWrapper.content);
		}), new KeyFrame(duration));
		timeline.setCycleCount(Animation.INDEFINITE);

		node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			eventWrapper.content = event;
			timeline.playFromStart();
		});

		node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> timeline.stop());
	}

}