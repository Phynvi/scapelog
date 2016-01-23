package com.scapelog.client.event.parser;

import com.google.common.primitives.Ints;
import com.scapelog.api.event.impl.GameMessageEvent;

public final class GameMessageParser extends EventParser<GameMessageEvent> {

	public GameMessageParser() {
		super(GameMessageEvent.class);
	}

	@Override
	public GameMessageEvent parse(String[] messageParts) {
		int type = Ints.tryParse(messageParts[1]);
		int flags = Ints.tryParse(messageParts[2]);
		String unformattedUsername = messageParts[3];
		String username = messageParts[4];
		String simpleUsername = messageParts[5];
		String message = messageParts[6];
		String channel = messageParts[7];
		int qcId = Ints.tryParse(messageParts[8]);
		return new GameMessageEvent(type, flags, unformattedUsername, username, simpleUsername, message, channel, qcId);
	}

}