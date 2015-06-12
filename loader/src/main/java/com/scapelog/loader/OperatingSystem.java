package com.scapelog.loader;

enum OperatingSystem {
	WINDOWS("http://download.oracle.com/otn-pub/java/jdk/8u45-b15/jre-8u45-windows-i586.tar.gz", "http://download.oracle.com/otn-pub/java/jdk/8u45-b15/jre-8u45-windows-x64.tar.gz"),
	LINUX("http://download.oracle.com/otn-pub/java/jdk/8u45-b14/jre-8u45-linux-i586.tar.gz", "http://download.oracle.com/otn-pub/java/jdk/8u45-b14/jre-8u45-linux-x64.tar.gz"),
	SOLARIS("http://download.oracle.com/otn-pub/java/jdk/8u45-b14/jre-8u45-solaris-x64.tar.gz", "http://download.oracle.com/otn-pub/java/jdk/8u45-b14/jre-8u45-solaris-x64.tar.gz"),
	MAC("http://download.oracle.com/otn-pub/java/jdk/8u45-b14/jre-8u45-macosx-x64.tar.gz", "http://download.oracle.com/otn-pub/java/jdk/8u45-b14/jre-8u45-macosx-x64.tar.gz"),
	GENERIC(null, null);

	private final String downloadLink32bit;
	private final String downloadLink64bit;

	OperatingSystem(String downloadLink32bit, String downloadLink64bit) {
		this.downloadLink32bit = downloadLink32bit;
		this.downloadLink64bit = downloadLink64bit;
	}

	public String getDownloadLink() {
		return is64bit() ? downloadLink64bit : downloadLink32bit;
	}

	public boolean is64bit() {
		boolean is64bit;
		if (System.getProperty("os.name").contains("Windows")) {
			is64bit = (System.getenv("ProgramFiles(x86)") != null);
		} else {
			is64bit = (System.getProperty("os.arch").contains("64"));
		}
		return is64bit;
	}

	public static String getExecutable(OperatingSystem operatingSystem) {
		switch (operatingSystem) {
			case WINDOWS:
				return "bin/java.exe";
			case MAC: // check
				return "Contents/Home/bin/java";
			case LINUX:
			case SOLARIS: // check
				return "bin/java";
		}
		return null;
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