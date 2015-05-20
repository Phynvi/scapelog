package com.scapelog.client.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.scapelog.client.util.ClassUtils;

import java.util.List;
import java.util.Map;

public final class ClientEventDispatcher {

	private static final Map<Class<? extends ClientEvent>, List<ClientEventListener<? extends ClientEvent>>> eventHandlers = Maps.newHashMap();

	public static void registerListener(ClientEventListener<? extends ClientEvent> listener) {
		Class<? extends ClientEvent> type = listener.getEventType();
		List<ClientEventListener<? extends ClientEvent>> listeners = eventHandlers.get(type);
		if (listeners == null) {
			listeners = Lists.newArrayList();
			eventHandlers.put(type, listeners);
		}
		listeners.add(listener);
	}

	public static void unregisterListener(ClientEventListener<? extends ClientEvent> listener) {
		Class<? extends ClientEvent> type = listener.getEventType();
		List<ClientEventListener<? extends ClientEvent>> listeners = eventHandlers.get(type);
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	public static <E extends ClientEvent> void fireEvent(E event) {
		if (!verifyEventSource(event)) {
			return;
		}
		Class<? extends ClientEvent> type = event.getClass();
		List<ClientEventListener<?>> listeners = eventHandlers.get(type);
		if (listeners == null)
			return;
		for (ClientEventListener<?> l : listeners) {
			@SuppressWarnings("unchecked")
			ClientEventListener<ClientEvent> listener = (ClientEventListener<ClientEvent>) l;
			listener.eventExecuted(event);
		}
	}

	private static <E extends ClientEvent> boolean verifyEventSource(E e) {
		if (!(e instanceof SourceVerifiedClientEvent)) {
			return true;
		}
		SourceVerifiedClientEvent event = (SourceVerifiedClientEvent) e;
		return ClassUtils.isSourceVerified(event.getSource());
	}

}