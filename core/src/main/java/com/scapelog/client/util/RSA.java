package com.scapelog.client.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public final class RSA {
	private static final BigInteger PUBLIC_KEY = new BigInteger("");
	private static final BigInteger MODULUS = new BigInteger("");

	private RSA() {

	}

	public static String encrypt(String message) {
		byte[] bytes;
		try {
			bytes = message.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			bytes = message.getBytes();
		}
		BigInteger msg = new BigInteger(bytes);
		return msg.modPow(PUBLIC_KEY, MODULUS).toString();
	}

}