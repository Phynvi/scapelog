package com.scapelog.client.reflection.wrappers;

import com.scapelog.client.reflection.ReflectedFields;
import com.scapelog.client.reflection.ReflectionField;
import com.scapelog.client.reflection.Wrapper;

public final class NPC extends Wrapper {

	private final ReflectionField<String> name = new ReflectionField<>(ReflectedFields.NPC_NAME, "");

	protected NPC(Object base) {
		// todo: find definition field
		name.initialize(base);
	}

	public String getName() {
		return getValue(name);
	}

}