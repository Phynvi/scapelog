package com.scapelog.client.event.impl;

import com.scapelog.api.event.Event;

public final class PrintEvent extends Event {

	private final String text;

	public PrintEvent(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}