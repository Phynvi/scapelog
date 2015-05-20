package com.scapelog.client.event;

import com.scapelog.api.ClientFeature;
import com.scapelog.api.event.Event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class ClientEventReceiver implements Runnable {
	private final static BlockingQueue<String> eventQueue = new LinkedBlockingQueue<>();

	public static void receive(String eventString) {
		try {
			eventQueue.add(eventString);
		} catch (Exception e) {
			/**/
		}
	}

	@Override
	public void run() {
		for (;;) {
			try {
				String eventString = eventQueue.take();
				String[] parts = eventString.split("_");
				if (parts.length > 0) {
					String identifier = parts[0];
					ClientFeature feature = ClientFeature.getFeature(identifier);
					if (feature == null) {
						continue;
					}
					if (feature.getStatus() == ClientFeatureStatus.UNVERIFIED) {
						feature.setStatus(ClientFeatureStatus.ENABLED);
					}
					Event event = ClientFeature.parseEvent(identifier, parts);
					if (event == null) {
						continue;
					}
					EventDispatcher.fireEvent(event);
				}
			} catch (InterruptedException e) {
				/**/
			}
		}
	}

}