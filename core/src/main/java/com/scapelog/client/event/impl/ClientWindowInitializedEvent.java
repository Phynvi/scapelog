package com.scapelog.client.event.impl;

import com.scapelog.client.event.ClientEvent;
import com.scapelog.client.ui.DecoratedFrame;

public final class ClientWindowInitializedEvent extends ClientEvent {

	private final DecoratedFrame frame;

	public ClientWindowInitializedEvent(DecoratedFrame frame) {
		this.frame = frame;
	}

	public DecoratedFrame getFrame() {
		return frame;
	}

}