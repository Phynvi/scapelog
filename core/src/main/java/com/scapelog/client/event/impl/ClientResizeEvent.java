package com.scapelog.client.event.impl;

import com.scapelog.client.event.ClientEvent;

public final class ClientResizeEvent extends ClientEvent {

	private final int width;

	private final int height;

	public ClientResizeEvent(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}