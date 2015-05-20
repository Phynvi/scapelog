package com.scapelog.client.event.impl;

import com.scapelog.client.event.ClientEventReceiver;
import com.scapelog.client.event.SourceVerifiedClientEvent;

public final class GameMessageEvent extends SourceVerifiedClientEvent {

	private final String message;
	private final int unused, type;

	public GameMessageEvent(String message, int unused, int type) {
		super(ClientEventReceiver.class);
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