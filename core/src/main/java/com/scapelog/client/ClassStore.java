package com.scapelog.client;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.util.Optional;

public final class ClassStore {

	private static final ObservableMap<String, Class<?>> classMap = FXCollections.observableHashMap();

	@SuppressWarnings("unused")
	public static void addClass(String name, Object clazz) {
		if (clazz.getClass().equals(Class.class)) {
			classMap.put(name, (Class) clazz);
		}
	}

	public static Optional<Class<?>> getClass(String name) {
		Class<?> clazz = classMap.get(name);
		return Optional.ofNullable(clazz);
	}

	public static void addListener(MapChangeListener<? super String, ? super Class<?>> listener) {
		classMap.addListener(listener);
	}

	public static int getClassCount() {
		return classMap.size();
	}

}