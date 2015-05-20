package com.scapelog.client.event.impl;

import com.scapelog.client.ClientFeatures;
import com.scapelog.api.ClientFeature;
import com.scapelog.client.event.SourceVerifiedClientEvent;

public final class FeatureEnableEvent extends SourceVerifiedClientEvent {

	private final ClientFeature feature;

	public FeatureEnableEvent(ClientFeature feature) {
		super(ClientFeatures.class);
		this.feature = feature;
	}

	public ClientFeature getFeature() {
		return feature;
	}

}