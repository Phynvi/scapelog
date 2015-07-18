package com.scapelog.client;

import com.scapelog.api.ClientFeature;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.api.ClientFeatureStatus;
import com.scapelog.client.event.impl.FeatureEnableEvent;

public final class ClientFeatures {

	public static void enable(ClientFeature feature) {
		ClientFeatureStatus status = feature.getStatus();
//		Debug.println("%s: %s", feature.getName(), status);
		if (status == ClientFeatureStatus.UNVERIFIED || status == ClientFeatureStatus.ENABLED) {
			return;
		} else {
			feature.setStatus(ClientFeatureStatus.UNVERIFIED);
		}
//		Debug.println("Enabling feature: %s", feature);
		ClientEventDispatcher.fireEvent(new FeatureEnableEvent(feature));
	}

	public static boolean isEnabled(ClientFeature feature) {
		return !feature.getStatus().equals(ClientFeatureStatus.DISABLED);
	}

}