package com.scapelog.api.event.impl;

import com.scapelog.client.event.SourceVerifiedEvent;
import com.scapelog.client.event.parser.GameMessageParser;

public final class GameMessageEvent extends SourceVerifiedEvent {

	private final String message;
	private final int unused, type;

	public GameMessageEvent(String message, int unused, int type) {
		super(GameMessageParser.class);
		this.message = message;
		this.unused = unused;
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public int getUnused() {
		return unused;
	}

	public int getType() {
		return type;
	}

}