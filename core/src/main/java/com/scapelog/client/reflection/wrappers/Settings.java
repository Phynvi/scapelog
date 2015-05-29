package com.scapelog.client.reflection.wrappers;

import com.google.common.collect.Maps;

import java.util.Map;

public final class Settings {
	public static final int SLAYER_ASSIGNMENT_KILLS_LEFT = 183;
	public static final int SLAYER_ASSIGNMENT_MOB_CATEGORY = 185;

	private static Map<Integer, Integer> settings = Maps.newHashMap();

	public static void set(int id, int value) {
		settings.put(id, value);
	}

	public static int get(int id) {
		return settings.getOrDefault(id, 0);
	}

}