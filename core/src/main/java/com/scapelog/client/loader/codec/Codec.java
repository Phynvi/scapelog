package com.scapelog.client.loader.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * @author Sean
 */
public abstract class Codec<T> {

	/**
	 * Reads the data from a {@link InputStream}.
	 * @param is The {@link InputStream}.
	 * @throws IOException The exception thrown if a i/o error occurs.
	 */
	public abstract T read(InputStream is) throws IOException;

	/**
	 * Saves the data of the codec.
	 * @param data The data to save.
	 * @param out The {@link OutputStream} to write to.
	 */
	public abstract void save(T data, OutputStream out) throws IOException;

	/**
	 * Reads data from a byte array.
	 * @param data The data to read.
	 * @throws IOException The exception thrown if a i/o error occurs.
	 */
	public T read(byte[] data) throws IOException {
		return read(new ByteArrayInputStream(data));
	}

	/**
	 * Reads a file from a url.
	 * @param url The url of the file.
	 * @throws IOException The exception thrown if a i/o error occurs.
	 */
	public T read(URL url) throws IOException {
		return read(url.openConnection().getInputStream());
	}

	/**
	 * Reads the data in a {@link InputStream} and returns the data as a byte array.
	 * Warning! you will need to close the {@link InputStream} yourself, this method doesn't close it!.
	 * @param input The {@link InputStream} to read.
	 * @return The data in the {@link InputStream}.
	 * @throws IOException The exception thrown if an i/o error occurs.
	 */
	public byte[] readInputStream(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			byte[] tmp = new byte[1024];
			int read;
			while ((read = input.read(tmp)) != -1) {
				output.write(tmp, 0, read);
			}
			output.flush();
		} finally {
			output.close();
		}
		return output.toByteArray();
	}

	/**
	 * Reads the data in a {@link InputStream} and returns the data as a byte array and closes the {@link InputStream}
	 * once its finished.
	 * @param input The {@link InputStream} to read.
	 * @return The data from the {@link InputStream}.
	 * @throws IOException
	 */
	public byte[] readAndCloseInputStream(InputStream input) throws IOException {
		try {
			return readInputStream(input);
		} finally {
			input.close();
		}
	}

	/**
	 * Saves the data to a byte array.
	 * @param data The data to save.
	 */
	public byte[] saveAsByteArray(T data) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		save(data, out);
		return out.toByteArray();
	}

}