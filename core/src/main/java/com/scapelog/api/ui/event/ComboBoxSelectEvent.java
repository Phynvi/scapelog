package com.scapelog.api.ui.event;

import javafx.scene.control.ComboBox;

@FunctionalInterface
public interface ComboBoxSelectEvent<T> {

	void selected(ComboBox<T> comboBox, T item);

}