package com.scapelog.client.event.impl;

import com.scapelog.client.ScapeLog;
import com.scapelog.client.event.SourceVerifiedClientEvent;
import com.scapelog.client.model.Language;
import com.scapelog.client.model.WorldList;

import java.util.Optional;

public final class ClientLoadEvent extends SourceVerifiedClientEvent {

	private final Optional<WorldList> world;

	private final Language language;

	public ClientLoadEvent(Optional<WorldList> world, Language language) {
		super(ScapeLog.class);
		this.world = world;
		this.language = language;
	}

	public Optional<WorldList> getWorld() {
		return world;
	}

	public Language getLanguage() {
		return language;
	}

}