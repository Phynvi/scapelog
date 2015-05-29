package com.scapelog.client.config;

import com.scapelog.client.ScapeLog;
import com.scapelog.client.util.Debug;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;

public final class Config {
	private static final Object LOCK = new Object();
	private static final Ini ini = new Ini();

	private static final String[] propertiesLocations = {
			System.getProperty("user.home") + "/.scapelog/scapelog.properties",
			"./scapelog.properties",
			System.getProperty("java.io.tmpdir") + "/scapelog.properties"
	};

	public static void load() {
		try {
			File file = new File(getPropertyLocation());
			boolean exists = true;
			if (!file.exists()) {
				exists = file.createNewFile();
			}
			if (!exists) {
				System.err.println("Failed to load config");
				return;
			}
			ini.load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		synchronized (LOCK) {
			try {
				File propertiesFile = new File(getPropertyLocation());
				ini.setComment(" ScapeLog config");
				ini.store(propertiesFile);
				Debug.println("config saved to %s", propertiesFile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getPropertyLocation() {
		String propertiesLocation = propertiesLocations[0];
		for (String location : propertiesLocations) {
			File file = new File(location);
			if (file.exists()) {
				propertiesLocation = location;
				break;
			} else {
				try {
					if (file.createNewFile()) {
						break;
					}
				} catch (Exception e) {
				/**/
				}
			}
		}
		if (ScapeLog.debug) {
			return propertiesLocations[1];
		}
		return propertiesLocation;
	}

	public static Profile.Section createSection(String key) {
		return ini.add(key);
	}

	public static Profile.Section get(String key) {
		return ini.get(key);
	}

	public static Profile.Section get(Object key, int index) {
		return ini.get(key, index);
	}

	private static String get(Object sectionName, Object optionName) {
		return ini.get(sectionName, optionName);
	}

	private static <T> T get(Object sectionName, Object optionName, Class<T> clazz) {
		return ini.get(sectionName, optionName, clazz);
	}

	public static boolean getBoolean(String section, String key) {
		return get(section, key, boolean.class);
	}

	public static boolean getBoolean(String section, String key, boolean defaultValue) {
		return getBooleanAdd(section, key, defaultValue, false);
	}

	public static boolean getBooleanOrAdd(String section, String key, boolean defaultValue) {
		return getBooleanAdd(section, key, defaultValue, true);
	}

	private static boolean getBooleanAdd(String section, String key, boolean defaultValue, boolean add) {
		Profile.Section sect = ini.get(section);
		boolean contains = sect != null && sect.containsKey(key);
		if (add && !contains) {
			ini.put(section, key, defaultValue);
		}
		if (sect == null) {
			return defaultValue;
		}
		if (contains) {
			try {
				return sect.get(key, boolean.class);
			} catch (IllegalArgumentException e) {
				setBoolean(section, key, defaultValue);
			}
		}
		return defaultValue;
	}

	public static String getString(String section, String key) {
		return get(section, key, String.class);
	}

	public static String getString(String section, String key, String defaultValue) {
		return getStringAdd(section, key, defaultValue, false);
	}

	public static String getStringOrAdd(String section, String key, String defaultValue) {
		return getStringAdd(section, key, defaultValue, true);
	}

	private static String getStringAdd(String section, String key, String defaultValue, boolean add) {
		Profile.Section sect = ini.get(section);
		boolean contains = sect != null && sect.containsKey(key);
		if (add && !contains) {
			ini.put(section, key, defaultValue);
		}
		if (sect == null) {
			return defaultValue;
		}
		if (contains) {
			try {
				return sect.get(key, String.class);
			} catch (IllegalArgumentException e) {
				setString(section, key, defaultValue);
			}
		}
		return defaultValue;
	}

	public static int getInt(String section, String key) {
		return get(section, key, int.class);
	}

	public static int getInt(String section, String key, int defaultValue) {
		return getIntAdd(section, key, defaultValue, false);
	}

	public static int getIntOrAdd(String section, String key, int defaultValue) {
		return getIntAdd(section, key, defaultValue, true);
	}

	private static int getIntAdd(String section, String key, int defaultValue, boolean add) {
		Profile.Section sect = ini.get(section);
		boolean contains = sect != null && sect.containsKey(key);
		if (add && !contains) {
			ini.put(section, key, defaultValue);
		}
		if (sect == null) {
			return defaultValue;
		}
		if (contains) {
			try {
				return sect.get(key, int.class);
			} catch (IllegalArgumentException e) {
				setInt(section, key, defaultValue);
			}
		}
		return defaultValue;
	}

	public static double getDouble(String section, String key) {
		return get(section, key, double.class);
	}

	public static double getDouble(String section, String key, double defaultValue) {
		return getDoubleAdd(section, key, defaultValue, false);
	}

	public static double getDoubleOrAdd(String section, String key, double defaultValue) {
		return getDoubleAdd(section, key, defaultValue, true);
	}

	private static double getDoubleAdd(String section, String key, double defaultValue, boolean add) {
		Profile.Section sect = ini.get(section);
		boolean contains = sect != null && sect.containsKey(key);
		if (add && !contains) {
			ini.put(section, key, defaultValue);
		}
		if (sect == null) {
			return defaultValue;
		}
		if (contains) {
			try {
				return sect.get(key, double.class);
			} catch (IllegalArgumentException e) {
				setDouble(section, key, defaultValue);
			}
		}
		return defaultValue;
	}

	public static long getLong(String section, String key) {
		return get(section, key, long.class);
	}

	public static long getLong(String section, String key, long defaultValue) {
		return getLongAdd(section, key, defaultValue, false);
	}

	public static long getLongOrAdd(String section, String key, long defaultValue) {
		return getLongAdd(section, key, defaultValue, true);
	}

	private static long getLongAdd(String section, String key, long defaultValue, boolean add) {
		Profile.Section sect = ini.get(section);
		boolean contains = sect != null && sect.containsKey(key);
		if (add && !contains) {
			ini.put(section, key, defaultValue);
		}
		if (sect == null) {
			return defaultValue;
		}
		if (contains) {
			try {
				return sect.get(key, long.class);
			} catch (IllegalArgumentException e) {
				setLong(section, key, defaultValue);
			}
		}
		return defaultValue;
	}

	private static void set(String section, String key, Object value) {
		ini.put(section, key, value);
	}

	public static void setBoolean(String section, String key, boolean value) {
		set(section, key, value);
	}

	public static void setString(String section, String key, String value) {
		set(section, key, value);
	}

	public static void setInt(String section, String key, int value) {
		set(section, key, value);
	}

	public static void setDouble(String section, String key, double value) {
		set(section, key, value);
	}

	public static void setLong(String section, String key, long value) {
		set(section, key, value);
	}

}