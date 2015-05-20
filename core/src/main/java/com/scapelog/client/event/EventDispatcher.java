package com.scapelog.client.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.scapelog.api.event.Event;
import com.scapelog.api.event.EventListener;

import java.util.List;
import java.util.Map;

public final class EventDispatcher {

	private static final Map<Class<? extends Event>, List<EventListener<? extends Event>>> eventHandlers = Maps.newHashMap();

	public static void registerListener(EventListener<? extends Event> listener) {
		Class<? extends Event> type = listener.getEventType();
		List<EventListener<? extends Event>> listeners = eventHandlers.get(type);
		if (listeners == null) {
			listeners = Lists.newArrayList();
			eventHandlers.put(type, listeners);
		}
		listeners.add(listener);
	}

	public static void unregisterListener(EventListener<? extends Event> listener) {
		Class<? extends Event> type = listener.getEventType();
		List<EventListener<? extends Event>> listeners = eventHandlers.get(type);
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	public static <E extends Event> void fireEvent(E event) {
		Class<? extends Event> type = event.getClass();
		List<EventListener<?>> listeners = eventHandlers.get(type);
		if (listeners == null)
			return;
		for (EventListener<?> l : listeners) {
			@SuppressWarnings("unchecked")
			EventListener<Event> listener = (EventListener<Event>) l;
			listener.eventExecuted(event);
		}
	}

}