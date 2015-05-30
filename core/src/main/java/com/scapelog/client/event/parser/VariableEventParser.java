package com.scapelog.client.event.parser;

import com.scapelog.api.event.impl.VariableEvent;
import com.scapelog.client.reflection.wrappers.Settings;

public final class VariableEventParser extends EventParser<VariableEvent> {

	public VariableEventParser() {
		super(VariableEvent.class);
	}

	@Override
	public VariableEvent parse(String[] messageParts) {
		int id = Integer.parseInt(messageParts[1]);
		int newValue = Integer.parseInt(messageParts[2]);
		int oldValue = Settings.get(id);
		Settings.set(id, newValue);
		return new VariableEvent(id, oldValue, newValue);
	}

}