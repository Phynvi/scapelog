package com.scapelog.client.event;

public abstract class SourceVerifiedClientEvent extends ClientEvent {

	private final Class<?> source;

	protected SourceVerifiedClientEvent(Class<?> source) {
		this.source = source;
	}

	public Class<?> getSource() {
		return source;
	}

}