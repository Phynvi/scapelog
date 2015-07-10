package com.scapelog.client.loader;

import com.scapelog.client.reflection.Reflection;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.HashMap;

public final class AppletClassLoader extends URLClassLoader {

	private static final HashMap<String, Class> LOADED = new HashMap<>();
	private static ClassLoader jagexClassLoader;

	public AppletClassLoader(URL... urls) {
		super(urls);
	}

	public static ClassLoader getJagexClassLoader() {
		return jagexClassLoader;
	}

	public static Class getJagexClass(String name) {
		return LOADED.get(name);
	}

	public static Class __defineClass(String name, byte[] buffer, int off, int len, ProtectionDomain protectionDomain, ClassLoader from) {
		if (jagexClassLoader == null)
			jagexClassLoader = from;
		try {
			Class c = Reflection.declaredMethod("defineClass")
					.in(from)
					.in(ClassLoader.class)
					.withParameters(String.class, byte[].class, int.class, int.class, ProtectionDomain.class)
					.withReturnType(Class.class)
					.invoke(name, buffer, 0, buffer.length, protectionDomain);
			LOADED.put(name, c);
			return c;
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
		return null;
	}

}