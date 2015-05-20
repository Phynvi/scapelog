package com.scapelog.client.reflection;

public final class ReflectionField<T> {

	private final ReflectedField reflectedField;

	private final T defaultValue;

	private Object baseObject;

	public ReflectionField(ReflectedField reflectedField, T defaultValue) {
		this.reflectedField = reflectedField;
		this.defaultValue = defaultValue;
	}

	public void initialize(Object baseObject) {
		this.baseObject = baseObject;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public boolean isInitialized() {
		return baseObject != null;
	}

	public Object getBaseObject() {
		return baseObject;
	}

	public ReflectedField getReflectedField() {
		return reflectedField;
	}

}