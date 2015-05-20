package com.scapelog.client.event;

import com.scapelog.api.event.Event;

public abstract class SourceVerifiedEvent extends Event {

	private final Class<?> source;

	protected SourceVerifiedEvent(Class<?> source) {
		this.source = source;
	}

	public Class<?> getSource() {
		return source;
	}

}