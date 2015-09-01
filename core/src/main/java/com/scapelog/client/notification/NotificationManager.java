package com.scapelog.client.notification;

import javafx.scene.Node;

public abstract class NotificationManager {

	private final String name;

	public NotificationManager(String name) {
		this.name = name;
	}

	public abstract Node getPanelContent();

	public String getName() {
		return name;
	}

}