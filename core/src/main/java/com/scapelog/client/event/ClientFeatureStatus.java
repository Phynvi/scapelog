package com.scapelog.client.event;

public enum ClientFeatureStatus {
	DISABLED("Disabled", "disabled"),
	UNVERIFIED("Unverified", "unverified"),
	ENABLED("Enabled", "enabled");

	private final String status;
	private final String styleClass;

	ClientFeatureStatus(String status, String styleClass) {
		this.status = status;
		this.styleClass = styleClass;
	}

	public String getStatus() {
		return status;
	}

	public String getStyleClass() {
		return styleClass;
	}

}