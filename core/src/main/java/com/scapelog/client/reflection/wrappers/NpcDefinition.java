package com.scapelog.client.reflection.wrappers;

import com.scapelog.client.reflection.ReflectedFields;
import com.scapelog.client.reflection.ReflectionField;
import com.scapelog.client.reflection.Wrapper;

public final class NpcDefinition extends Wrapper {

	private final ReflectionField<Integer> combatLevel = new ReflectionField<>(ReflectedFields.NPC_COMBAT_LEVEL, 0);

	public NpcDefinition(Object base) {
		combatLevel.initialize(base);
	}

	public int getCombatLevel() {
		return getValue(combatLevel);
	}

}