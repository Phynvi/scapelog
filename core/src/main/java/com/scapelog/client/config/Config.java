package com.scapelog.client.config;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.scapelog.api.Setting;
import com.scapelog.client.ScapeLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

public final class Config {

	private static ConnectionSource connectionSource;
	private static Dao<Setting, String> settingsDao;

	public static void setup() throws SQLException {
		if (!ScapeLog.debug) {
			System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
		}

		Path databasePath = Paths.get(System.getProperty("user.home") + "/.scapelog/db.sqlite");
		if (!Files.exists(databasePath)) {
			try {
				Files.createFile(databasePath);
			} catch (IOException e) {
				System.err.println("Failed to create db.sqlite in path " + databasePath.toString());
				e.printStackTrace();
				return;
			}
		}

		connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + databasePath.toString());

		settingsDao = DaoManager.createDao(connectionSource, Setting.class);
		settingsDao.setObjectCache(true);

//		TableUtils.dropTable(connectionSource, Setting.class, false);
		TableUtils.createTableIfNotExists(connectionSource, Setting.class);
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

	public static <T> T runBatch(Callable<T> callable) throws Exception {
		return settingsDao.callBatchTasks(callable);
	}

	public static <T> T runTransactional(Callable<T> callable) throws SQLException {
		return TransactionManager.callInTransaction(connectionSource, callable);
	}

}