package com.scapelog.client.util;

public final class ClassUtils {

	public static boolean isSourceVerified(Class<?> required) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement source = stackTrace[4];
		return source.getClassName().equals(required.getName());
	}

	private ClassUtils() {

	}

}