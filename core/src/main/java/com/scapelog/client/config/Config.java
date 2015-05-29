package com.scapelog.client.config;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public final class Config {

	private static ConnectionSource connectionSource;
	private static Dao<Setting, String> settingsDao;

	public static void setup() throws SQLException {
//		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");

		connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + System.getProperty("user.home") + "/.scapelog/db.sqlite");
		settingsDao = DaoManager.createDao(connectionSource, Setting.class);

//		TableUtils.dropTable(connectionSource, Setting.class, false);
		TableUtils.createTableIfNotExists(connectionSource, Setting.class);
	}

	public static void save() {

	}

	public static void setBoolean(String sectionName, String key, boolean value) {
		setString(sectionName, key, Boolean.toString(value));
	}

	public static boolean getBoolean(String sectionName, String key, boolean defaultValue) {
		String value = getString(sectionName, key, null);
		return value == null ? defaultValue : Boolean.parseBoolean(value);
	}

	public static boolean getBooleanOrAdd(String sectionName, String key, boolean defaultValue) {
		String value = getString(sectionName, key, null);
		if (value == null) {
			setBoolean(sectionName, key, defaultValue);
		}
		return value == null ? defaultValue : Boolean.parseBoolean(value);
	}

	public static void setInt(String sectionName, String key, int value) {
		setString(sectionName, key, Integer.toString(value));
	}

	public static int getIntOrAdd(String sectionName, String key, int defaultValue) {
		String value = getString(sectionName, key, null);
		if (value == null) {
			setInt(sectionName, key, defaultValue);
		}
		return value == null ? defaultValue : Integer.parseInt(value);
	}

	public static String getString(String key, String defaultValue) {
		try {
			List<Setting> results = settingsDao.queryForEq("key", key);
			if (results.isEmpty()) {
				return defaultValue;
			}
			Setting setting = results.get(0);
			return setting.value;
		} catch (SQLException e) {
			return defaultValue;
		}
	}

	public static String getString(String section, String key, String defaultValue) {
		try {
			List<Setting> results = settingsDao.queryBuilder().where().eq("section", section).and().eq("key", key).query();
			if (results.isEmpty()) {
				return defaultValue;
			}
			Setting setting = results.get(0);
			return setting.value;
		} catch (SQLException e) {
			return defaultValue;
		}
	}

	public static void setString(String section, String key, String value) {
		try {
			Setting setting = new Setting();
			setting.section = section;
			setting.key = key;
			setting.value = value;
			settingsDao.createOrUpdate(setting);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}