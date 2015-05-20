package com.scapelog.api.event;

public abstract class EventListener<E extends Event> {

	private final Class<E> eventType;

	public EventListener(Class<E> eventType) {
		this.eventType = eventType;
	}

	public abstract void eventExecuted(E event);

	public final Class<E> getEventType() {
		return eventType;
	}

}