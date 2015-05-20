package com.scapelog.client.event.impl;

import com.scapelog.client.event.ClientEvent;

public final class LoadingEvent extends ClientEvent {

	private final String message;

	public LoadingEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}