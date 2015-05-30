package com.scapelog.client.model;

import com.scapelog.client.config.Config;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public enum Language {
	ENGLISH("English", 0),
	GERMAN("German", 1),
	FRENCH("French", 2),
	PORTUGESE("Portugese", 3),
	SPANISH("Spanish", 6);

	private static ObservableList<Language> list;

	private final String name;
	private final int language;

	Language(String name, int language) {
		this.name = name;
		this.language = language;
	}

	public int getLanguage() {
		return language;
	}

	public String getName() {
		return name;
	}

	public static ObservableList<Language> asList() {
		if (list == null) {
			list = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(Language.values()));
		}
		return list;
	}

	public static Language getLanguage(String name) {
		for (Language language : asList()) {
			if (language.name.equalsIgnoreCase(name)) {
				return language;
			}
		}
		return Language.ENGLISH;
	}

	public static Language getSavedLanguage() {
		return getLanguage(Config.getString("client", "language", "english"));
	}

	public static void saveLanguage(Language language) {
		Config.setString("client", "language", language.getName().toLowerCase());
	}

	@Override
	public String toString() {
		return name;
	}

}