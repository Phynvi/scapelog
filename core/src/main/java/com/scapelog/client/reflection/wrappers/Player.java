package com.scapelog.client.reflection.wrappers;

import com.scapelog.client.reflection.ReflectedFields;
import com.scapelog.client.reflection.ReflectionField;
import com.scapelog.client.reflection.Wrapper;

public final class Player extends Wrapper {

	private final ReflectionField<String> username = new ReflectionField<>(ReflectedFields.PLAYER_USERNAME, "");

	private Player(Object base) {
		username.initialize(base);
	}

	public String getName() {
		return getValue(username);
	}

	public static Player create(Object base) {
		return new Player(base);
	}

	@Override
	public String toString() {
		return "Player{name=" + getName() + "}";
	}
}