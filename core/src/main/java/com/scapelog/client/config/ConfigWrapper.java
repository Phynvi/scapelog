package com.scapelog.client.config;

import java.util.Optional;

public class ConfigWrapper {

	private final Optional<String> sectionName;

	public ConfigWrapper(Optional<String> sectionName) {
		this.sectionName = sectionName;
	}

	protected final int getInt(String key) {
		ensureSectionIsPresent();
		return Config.getInt(getSectionName(), key);
	}

	protected final int getIntOrAdd(String key, int value) {
		ensureSectionIsPresent();
		return Config.getIntOrAdd(getSectionName(), key, value);
	}

	protected final void setInt(String key, int value) {
		ensureSectionIsPresent();
		Config.setInt(getSectionName(), key, value);
	}

	protected final boolean getBoolean(String key) {
		ensureSectionIsPresent();
		return Config.getBoolean(getSectionName(), key);
	}

	protected final boolean getBooleanOrAdd(String key, boolean value) {
		ensureSectionIsPresent();
		return Config.getBooleanOrAdd(getSectionName(), key, value);
	}

	protected final void setBoolean(String key, boolean value) {
		ensureSectionIsPresent();
		Config.setBoolean(getSectionName(), key, value);
	}

	protected final double getDouble(String key) {
		ensureSectionIsPresent();
		return Config.getDouble(getSectionName(), key);
	}

	protected final double getDoubleOrAdd(String key, double value) {
		ensureSectionIsPresent();
		return Config.getDoubleOrAdd(getSectionName(), key, value);
	}

	protected final void setDouble(String key, double value) {
		ensureSectionIsPresent();
		Config.setDouble(getSectionName(), key, value);
	}

	protected final String getString(String key) {
		ensureSectionIsPresent();
		return Config.getString(getSectionName(), key);
	}

	protected final String getStringOrAdd(String key, String value) {
		ensureSectionIsPresent();
		return Config.getStringOrAdd(getSectionName(), key, value);
	}

	protected final void setString(String key, String value) {
		ensureSectionIsPresent();
		Config.setString(getSectionName(), key, value);
	}

	protected final long getLong(String key) {
		ensureSectionIsPresent();
		return Config.getLong(getSectionName(), key);
	}

	protected final long getLongOrAdd(String key, long value) {
		ensureSectionIsPresent();
		return Config.getLongOrAdd(getSectionName(), key, value);
	}

	protected final void setLong(String key, long value) {
		ensureSectionIsPresent();
		Config.setLong(getSectionName(), key, value);
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