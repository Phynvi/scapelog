package com.scapelog.client.config;

import java.util.Optional;

public class ConfigWrapper {

	private final Optional<String> sectionName;

	public ConfigWrapper(Optional<String> sectionName) {
		this.sectionName = sectionName;
	}

	protected final void setBoolean(String key, boolean value) {
		ensureSectionIsPresent();
		Config.setBoolean(sectionName.get(), key, value);
	}

	protected final boolean getBooleanOrAdd(String key, boolean defaultValue) {
		ensureSectionIsPresent();
		return Config.getBooleanOrAdd(sectionName.get(), key, defaultValue);
	}

	protected final void setInt(String key, int value) {
		ensureSectionIsPresent();
		Config.setInt(sectionName.get(), key, value);
	}

	protected final int getIntOrAdd(String key, int value) {
		ensureSectionIsPresent();
		return Config.getIntOrAdd(sectionName.get(), key, value);
	}

	protected final String getString(String key, String defaultValue) {
		ensureSectionIsPresent();
		return Config.getString(sectionName.get(), key, defaultValue);
	}

	protected final String getSectionName() {
		return sectionName.get();
	}

	protected final boolean hasSettings() {
		return sectionName.isPresent();
	}

	private void ensureSectionIsPresent() {
		if (!sectionName.isPresent()) {
			throw new IllegalStateException("Trying to use settings in a setting-less plugin");
		}
	}

}