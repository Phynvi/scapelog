package com.scapelog.loader;

final class Dependency {

	private final String type;
	private final String fileName;
	private final String checksum;
	private final String url;

	public Dependency(String type, String fileName, String checksum, String url) {
		this.type = type;
		this.checksum = checksum;
		this.fileName = fileName;
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public String getFileName() {
		return fileName;
	}

	public String getChecksum() {
		return checksum;
	}

	public String getUrl() {
		return url;
	}

}