package com.scapelog.client.event;

public abstract class ClientEventListener<E extends ClientEvent> {

	private final Class<E> eventType;

	public ClientEventListener(Class<E> eventType) {
		this.eventType = eventType;
	}

	public abstract void eventExecuted(E event);

	public final Class<E> getEventType() {
		return eventType;
	}

}