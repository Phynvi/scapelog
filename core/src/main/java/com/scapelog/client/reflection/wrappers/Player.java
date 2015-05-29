package com.scapelog.client.reflection.wrappers;

import com.scapelog.client.reflection.ReflectedFields;
import com.scapelog.client.reflection.ReflectionField;
import com.scapelog.client.reflection.Wrapper;

public final class Player extends Wrapper {

	private final ReflectionField<String> username = new ReflectionField<>(ReflectedFields.PLAYER_USERNAME, "");
	private final ReflectionField<Integer> combatLevel = new ReflectionField<>(ReflectedFields.PLAYER_COMBAT_LEVEL, 3);

	private Player(Object base) {
		username.initialize(base);
		combatLevel.initialize(base);
	}

	public String getName() {
		return getValue(username);
	}

	public int getCombatLevel() {
		return getValue(combatLevel);
	}

	public static Player create(Object base) {
		return new Player(base);
	}

	@Override
	public String toString() {
		return "Player{name=" + getName() + "}";
	}
}