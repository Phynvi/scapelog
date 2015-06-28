package com.scapelog.tools;

import com.google.common.base.Preconditions;

public abstract class Tool {

	private final String trigger;
	private final String name;
	private final String description;

	public Tool(String trigger, String name, String description) {
		this.trigger = Preconditions.checkNotNull(trigger);
		this.name = Preconditions.checkNotNull(name);
		this.description = Preconditions.checkNotNull(description);
		Preconditions.checkArgument(!trigger.contains(" "), "Trigger can not contain spaces!");
	}

	public String getTrigger() {
		return trigger;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public abstract void run();

}