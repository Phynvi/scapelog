package com.scapelog.api.event.impl;

import com.scapelog.client.event.SourceVerifiedEvent;
import com.scapelog.client.event.parser.IdleResetParser;

public final class IdleResetEvent extends SourceVerifiedEvent {

	public IdleResetEvent() {
		super(IdleResetParser.class);
	}

}