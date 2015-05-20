package com.scapelog.api.ui.event;

@FunctionalInterface
public interface ComboBoxSelectEvent<T> {

	void selected(T item);

}