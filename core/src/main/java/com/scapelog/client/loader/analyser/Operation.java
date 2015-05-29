package com.scapelog.client.loader.analyser;

public abstract class Operation {

	private final OperationAttributes attributes;

	public Operation(OperationAttributes attributes) {
		this.attributes = attributes;
	}

	public final OperationAttributes getAttributes() {
		return attributes;
	}

}