package com.scapelog.client.event.impl;

import com.scapelog.client.event.SourceVerifiedClientEvent;
import com.scapelog.api.plugin.Plugin;
import com.scapelog.client.plugins.PluginLoader;

public final class PluginStartEvent extends SourceVerifiedClientEvent {

	private final Plugin plugin;

	public PluginStartEvent(Plugin plugin) {
		super(PluginLoader.class);
		this.plugin = plugin;
	}

	public Plugin getPlugin() {
		return plugin;
	}

}