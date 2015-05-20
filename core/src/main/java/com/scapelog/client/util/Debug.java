package com.scapelog.client.util;

// println wrapper that the obfuscators will remove
public final class Debug {

	private Debug() {

	}

	public static void println(String format, Object... args) {
		System.out.println(String.format(format, args));
	}

	public static void println(Object str) {
		System.out.println(str);
	}

}