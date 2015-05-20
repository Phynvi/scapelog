package com.scapelog.loader;

final class BootFlag {

	private final String flag;

	private final String description;

	private final Runnable action;

	public BootFlag(String flag, String description, Runnable action) {
		this.flag = flag;
		this.description = description;
		this.action = action;
	}

	public String getFlag() {
		return flag;
	}

	public String getDescription() {
		return description;
	}

	public Runnable getAction() {
		return action;
	}

}