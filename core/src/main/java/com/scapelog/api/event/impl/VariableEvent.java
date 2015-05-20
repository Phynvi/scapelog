package com.scapelog.api.event.impl;

import com.scapelog.client.event.SourceVerifiedEvent;
import com.scapelog.client.event.parser.VariableEventParser;

public final class VariableEvent extends SourceVerifiedEvent {

	private final int id;

	private final int value;

	public VariableEvent(int id, int value) {
		super(VariableEventParser.class);
		this.id = id;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public int getValue() {
		return value;
	}

}