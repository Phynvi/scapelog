package com.scapelog.client.util;

import com.scapelog.client.ScapeLog;

// println wrapper that the obfuscators will remove
public final class Debug {

	private Debug() {

	}

	public static void println(String format, Object... args) {
		if (ScapeLog.debug) {
			System.out.println(String.format(format, args));
		}
	}

	public static void println(Object str) {
		if (ScapeLog.debug) {
			System.out.println(str);
		}
	}

}