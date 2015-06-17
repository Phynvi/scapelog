package com.scapelog.client.loader;

public final class LibraryModifier {

	public static byte[] modifyLibrary(String name, byte[] payload) {
		System.out.println("hello i want to modify a library called '" + name + "', it has " + payload.length + " bytes");
		return payload;
	}

}