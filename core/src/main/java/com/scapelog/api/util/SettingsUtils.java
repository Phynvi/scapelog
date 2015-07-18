package com.scapelog.api.util;

import com.scapelog.api.plugin.Plugin;
import com.scapelog.api.ui.NumberTextField;
import com.scapelog.api.ui.SpinnerControl;
import com.scapelog.api.ui.event.CheckBoxSelectEvent;
import com.scapelog.api.ui.event.ComboBoxSelectEvent;
import com.scapelog.client.config.Config;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.util.Optional;

public final class SettingsUtils {

	public static HBox createSpinnerSetting(String title, int min, int max, int value, ChangeListener<Double> changeListener) {
		SpinnerControl spinner = new SpinnerControl();
		spinner.setValue(value);
		spinner.setMinValue(min);
		spinner.setMaxValue(max);
		spinner.addChangeListener(changeListener);
		return Components.createBox(title, spinner);
	}

	public static HBox createButtonSetting(String title, String buttonTitle, EventHandler<ActionEvent> eventHandler) {
		Button button = Components.createBorderedButton(buttonTitle);
		button.setOnAction(eventHandler);
		return Components.createBox(title, button);
	}

	public static HBox createNumberFieldSetting(String title, int value, ChangeListener<Number> listener) {
		NumberTextField textField = new NumberTextField(BigDecimal.valueOf(value));
		textField.numberProperty().addListener(listener);

		return Components.createBox(title, textField);
	}

	public static <T> HBox createComboBoxSetting(String title, ObservableList<T> items, Optional<Double> prefWidth, T selectedItem, ComboBoxSelectEvent<T> selectEvent) {
		ComboBox<T> box = new ComboBox<>(items);
		if (selectEvent != null) {
			box.setOnAction(e -> selectEvent.selected(box, box.getSelectionModel().getSelectedItem()));
		}
		if (prefWidth.isPresent()) {
			box.setPrefWidth(prefWidth.get());
		}
		if (selectedItem != null) {
			box.getSelectionModel().select(selectedItem);
		}
		return Components.createBox(2, title, box);
	}

	public static HBox createShowButtonSetting(String sectionName, Plugin plugin) {
		return createCheckBoxSetting("Show button", (selected) -> {
			plugin.showButton(selected);
			Config.setBoolean(sectionName, "show-button", selected);
		}, Config.getBooleanOrAdd(sectionName, "show-button", true));
	}

	public static HBox createCheckBoxSetting(String title, CheckBoxSelectEvent selectEvent) {
		return createCheckBoxSetting(title, selectEvent, false);
	}

	public static HBox createCheckBoxSetting(String title, CheckBoxSelectEvent selectEvent, boolean selected) {
		CheckBox box = new CheckBox();
		box.setMaxHeight(Double.MAX_VALUE);
		box.setSelected(selected);
		box.setOnAction(e -> selectEvent.selected(box.isSelected()));
		return Components.createBox(3, title, box);
	}

	private SettingsUtils() {

	}

}