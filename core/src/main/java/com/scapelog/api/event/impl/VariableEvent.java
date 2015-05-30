package com.scapelog.api.event.impl;

import com.scapelog.client.event.SourceVerifiedEvent;
import com.scapelog.client.event.parser.VariableEventParser;

public final class VariableEvent extends SourceVerifiedEvent {

	private final int id;

	private final int oldValue;

	private final int newValue;

	public VariableEvent(int id, int oldValue, int newValue) {
		super(VariableEventParser.class);
		this.id = id;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public int getId() {
		return id;
	}

	public int getOldValue() {
		return oldValue;
	}

	public int getNewValue() {
		return newValue;
	}

}