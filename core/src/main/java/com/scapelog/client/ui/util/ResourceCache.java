package com.scapelog.client.ui.util;

import java.util.HashMap;
import java.util.Map;

public final class ResourceCache {

	private final static Map<Object, Object> cache;

	static {
		cache = new HashMap<>();
	}

	public static <T> Object retrieve(T type) {
		return receive(type, null);
	}

	public static <T> Object receive(T type, T defaultValue) {
		if (!contains(type)) {
			return defaultValue;
		}
		return cache.get(type);
	}

	public static <T> void store(T type, T value) {
		cache.put(type, value);
	}

	public static <T> boolean contains(T type) {
		return cache.containsKey(type);
	}

}