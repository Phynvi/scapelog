package com.scapelog.client.util;

import java.math.BigInteger;

public final class RSA {
	private static final BigInteger PUBLIC_KEY = new BigInteger("65537");
	private static final BigInteger MODULUS = new BigInteger("4213478309119431064218323995962389210721666869979509729860118007368600468970073722850459118558650604901403086106517841848560063352924705213846717796022761477055370486329226142133450375317050740349302998427110365268010999768527882802303682127878206868290725131274703151487333656900363210185285793443421");

	private RSA() {

	}

	public static String encrypt(String message) {
		byte[] bytes;
		try {
			bytes = message.getBytes("UTF-8");
		} catch (Exception e) {
			bytes = message.getBytes();
		}
		BigInteger msg = new BigInteger(bytes);
		return msg.modPow(PUBLIC_KEY, MODULUS).toString();
	}

}