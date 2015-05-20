package com.scapelog.client.event.parser;

import com.google.common.primitives.Ints;
import com.scapelog.api.event.impl.GameMessageEvent;

public final class GameMessageParser extends EventParser<GameMessageEvent> {

	public GameMessageParser() {
		super(GameMessageEvent.class);
	}

	@Override
	public GameMessageEvent parse(String[] messageParts) {
		String message = messageParts[1];
		int flags;
		int type;
		if (isInt(messageParts[messageParts.length - 1])) {
			flags = Integer.parseInt(messageParts[messageParts.length - 2]);
			type = Integer.parseInt(messageParts[messageParts.length - 1]);
		} else {
			flags = Integer.parseInt(messageParts[2]);
			type = Integer.parseInt(messageParts[3]);
		}
		return new GameMessageEvent(message, flags, type);
	}

	private boolean isInt(String message) {
		return Ints.tryParse(message) != null;
	}

}