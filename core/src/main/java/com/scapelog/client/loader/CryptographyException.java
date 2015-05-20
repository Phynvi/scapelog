package com.scapelog.client.loader;

/**
 * @author Sean
 */
public final class CryptographyException extends Exception {

	/**
	 * Creates a new {@link CryptographyException}.
	 * @param cause The {@link Exception} thrown.
	 */
	public CryptographyException(Exception cause) {
		super(cause);
	}

}