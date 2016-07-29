package com.scapelog.client.event;

import java.util.Arrays;

import com.scapelog.agent.util.InjectionUtils;
import com.scapelog.api.ClientFeature;
import com.scapelog.api.ClientFeatureStatus;
import com.scapelog.api.event.Event;
import com.scapelog.client.ScapeLog;

public final class ClientEventReceiver {

	public static void receive(String eventString) {
		ScapeLog.getExecutor().submit(() -> {
			try {
				String[] parts = eventString.split(InjectionUtils.SEPARATOR);
				if (parts.length > 0) {
					String identifier = parts[0];
					ClientFeature feature = ClientFeature.getFeature(identifier);
					if (feature == null) {
						return;
					}
					if (feature.getStatus() == ClientFeatureStatus.UNVERIFIED) {
						feature.setStatus(ClientFeatureStatus.ENABLED);
					}
					Event event = ClientFeature.parseEvent(identifier, parts);
					if (event == null) {
						return;
					}
					if (identifier.equals(ClientFeature.GAME_MESSAGES.getIdentifier())) {
						System.out.println(Arrays.toString(parts));
					}
					EventDispatcher.fireEvent(event);
				}
			} catch (Exception e) {
				/**/
			}
		});
	}

}