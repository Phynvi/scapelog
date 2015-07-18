package com.scapelog.client.util;

public enum OperatingSystem {
	WINDOWS, LINUX, SOLARIS, MAC, GENERIC;

	public boolean is64bit() {
		boolean is64bit;
		if (System.getProperty("os.name").contains("Windows")) {
			is64bit = (System.getenv("ProgramFiles(x86)") != null);
		} else {
			is64bit = (System.getProperty("os.arch").contains("64"));
		}
		return is64bit;
	}

	public static OperatingSystem getOperatingSystem() {
		String name = System.getProperty("os.name", "generic").toLowerCase();
		if (name.startsWith("windows")) {
			return OperatingSystem.WINDOWS;
		} else if (name.startsWith("linux")) {
			return OperatingSystem.LINUX;
		} else if (name.startsWith("sunos")) {
			return OperatingSystem.SOLARIS;
		} else if (name.startsWith("mac") || name.startsWith("darwin")) {
			return OperatingSystem.MAC;
		}
		return OperatingSystem.GENERIC;
	}

}