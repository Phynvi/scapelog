package com.scapelog.client.reflection;

import java.util.Optional;

public class ReflectedField {

	private Optional<String> className = Optional.empty();

	private Optional<String> fieldName = Optional.empty();

	public ReflectedField setClassName(String className) {
		if (this.className.isPresent()) {
			return this;
		}
		this.className = Optional.of(className);
		return this;
	}

	public ReflectedField setFieldName(String fieldName) {
		if (this.fieldName.isPresent()) {
			return this;
		}
		this.fieldName = Optional.of(fieldName);
		return this;
	}

	public Optional<String> getClassName() {
		return className;
	}

	public Optional<String> getFieldName() {
		return fieldName;
	}

}
