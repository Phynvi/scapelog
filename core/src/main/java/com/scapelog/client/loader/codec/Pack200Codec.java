package com.scapelog.client.loader.codec;

import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Sean
 */
public final class Pack200Codec extends Codec<byte[]> {

	@Override
	public byte[] read(InputStream is) throws IOException {
		return readAndCloseInputStream(new Pack200CompressorInputStream(new GZIPInputStream(is)));
	}

	@Override
	public void save(byte[] data, OutputStream out) throws IOException {
		Pack200CompressorOutputStream os = new Pack200CompressorOutputStream(new GZIPOutputStream(out));
		try {
			os.write(data);
		} finally {
			os.close();
		}
	}
}