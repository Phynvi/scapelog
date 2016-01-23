package com.scapelog.api.event.impl;

import com.scapelog.client.event.SourceVerifiedEvent;
import com.scapelog.client.event.parser.GameMessageParser;

public final class GameMessageEvent extends SourceVerifiedEvent {

	private final String unformattedUsername, username, simpleUsername, message, channel;
	private final int type, flags, qcId;

	public GameMessageEvent(int type, int flags, String unformattedUsername, String username, String simpleUsername, String message, String channel, int qcId) {
		super(GameMessageParser.class);
		this.type = type;
		this.flags = flags;
		this.unformattedUsername = unformattedUsername;
		this.username = username;
		this.simpleUsername = simpleUsername;
		this.message = message;
		this.channel = channel;
		this.qcId = qcId;
	}

	public int getType() {
		return type;
	}

	public int getFlags() {
		return flags;
	}

	public String getUnformattedUsername() {
		return unformattedUsername;
	}

	public String getUsername() {
		return username;
	}

	public String getSimpleUsername() {
		return simpleUsername;
	}

	public String getMessage() {
		return message;
	}

	public String getChannel() {
		return channel;
	}

	public int getQcId() {
		return qcId;
	}

}