package com.scapelog.client.reflection;

import com.scapelog.client.ClassStore;

import java.util.Optional;

public abstract class Wrapper {

	protected static  <T> T getValue(ReflectionField<T> field) {
		T value = field.getDefaultValue();
		ReflectedField reflectedField = field.getReflectedField();
		if (!reflectedField.getClassName().isPresent() || !reflectedField.getFieldName().isPresent()) {
			return value;
		}
		String className = reflectedField.getClassName().get();
		String fieldName = reflectedField.getFieldName().get();

		Optional<Class<?>> clazz = ClassStore.getClass(className);
		if (!clazz.isPresent()) {
			return value;
		}
		try {
			Object object = clazz.get();
			if (field.isInitialized()) {
				object = field.getBaseObject();
			}
			Reflection.FieldContainer<T> f = Reflection.declaredField(fieldName);
			value = f.in(object).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

}