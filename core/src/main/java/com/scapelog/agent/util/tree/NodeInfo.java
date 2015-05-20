package com.scapelog.agent.util.tree;

public abstract class NodeInfo<T> {

	private final T owner;

	private final String fieldName;

	private final String description;

	public NodeInfo(T owner, String fieldName, String description) {
		this.owner = owner;
		this.fieldName = fieldName;
		this.description = description;
	}

	public final T getOwner() {
		return owner;
	}

	public final String getName() {
		return fieldName;
	}

	public final String getDescription() {
		return description;
	}

}