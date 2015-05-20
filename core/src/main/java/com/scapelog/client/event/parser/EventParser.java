package com.scapelog.client.event.parser;

import com.scapelog.api.event.Event;

public abstract class EventParser<E extends Event> {

	private final Class<E> type;

	public EventParser(Class<E> type) {
		this.type = type;
	}

	public abstract E parse(String[] messageParts);

	public Class<E> getType() {
		return type;
	}

}