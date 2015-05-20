package com.scapelog.client.event.parser;

import com.scapelog.api.event.impl.IdleResetEvent;

public final class IdleResetParser extends EventParser<IdleResetEvent> {

	public IdleResetParser() {
		super(IdleResetEvent.class);
	}

	@Override
	public IdleResetEvent parse(String[] messageParts) {
		return new IdleResetEvent();
	}

}