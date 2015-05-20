package com.scapelog.client.ui.util;

import javafx.collections.ObservableList;

import java.net.URL;

public final class CSS {

	public static void addDefaultStyles(ObservableList<String> styleSheets) {
		addStylesheets(CSS.class, styleSheets, "/css/style.css");
	}

	public static URL getStylesheet(Class<?> base, String path) {
		return base.getResource(path);
	}

	public static void addStylesheets(Class<?> base, ObservableList<String> list, String... paths) {
		for (String path : paths) {
			URL sheet = getStylesheet(base, path);
			if (sheet != null) {
				list.add(sheet.toExternalForm());
			}
		}
	}

}
