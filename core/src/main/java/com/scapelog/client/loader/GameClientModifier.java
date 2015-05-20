package com.scapelog.client.loader;

import com.scapelog.client.loader.archive.JarArchive;
import com.scapelog.client.loader.codec.AESCodec;
import com.scapelog.client.loader.codec.JarArchiveCodec;
import com.scapelog.client.loader.codec.Pack200Codec;
import com.scapelog.client.util.JagBase64;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.jar.JarFile;

/**
 * @author Sean
 */
public final class GameClientModifier {

	/**
	 * The client name inside the gamepack.
	 */
	private static final String CLIENT_NAME = "inner.pack.gz";

	/**
	 * A {@link JarArchiveCodec} for loading the data in a {@link JarFile}.
	 */
	private final JarArchiveCodec jarArchiveCodec = new JarArchiveCodec();

	/**
	 * The {@link AESCodec}.
	 */
	private AESCodec aesCodec;

	/**
	 * Initialise the client controller.
	 * @throws IOException The exception thrown if an error occurs reading the gamepack archive.
	 * @throws CryptographyException The exception thrown if an cryptography error occurs.
	 */
	public GamePackArchives unpack(JavConfig config, Path gamePackPath) throws IOException, CryptographyException {
		byte[] key = JagBase64.decode(config.getParameter(JavConfig.SECRET_PARAMETER_NAME));
		byte[] iv = JagBase64.decode(config.getParameter(JavConfig.VECTOR_PARAMETER_NAME));
		this.aesCodec = new AESCodec(key, iv);
		JarArchive gamePackArchive = jarArchiveCodec.read(new FileInputStream(gamePackPath.toString()));
		byte[] unpackedClient = unpackClient(gamePackArchive.get(CLIENT_NAME));
		JarArchive clientArchive = jarArchiveCodec.read(unpackedClient);
		return new GamePackArchives(gamePackArchive, clientArchive);
	}

	/**
	 * Unpacks the client.
	 * @param encrypted The encrypted client.
	 * @return The decrypted client.
	 * @throws IOException The exception thrown if a i/o error occurs.
	 */
	private byte[] unpackClient(byte[] encrypted) throws IOException {
		byte[] decrypted = aesCodec.read(encrypted);
		return new Pack200Codec().read(decrypted);
	}

}