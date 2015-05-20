package com.scapelog.client.loader.codec;

import com.scapelog.client.loader.archive.JarArchive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

/**
 * @author Sean
 */
public final class JarArchiveCodec extends Codec<JarArchive> {

	@Override
	public JarArchive read(InputStream is) throws IOException {
		JarArchive jarArchive = new JarArchive();
		try (JarInputStream jis = new JarInputStream(is)) {
			JarEntry entry;
			while ((entry = jis.getNextJarEntry()) != null) {
				String name = entry.getName();
				if (!name.endsWith("/")) {
					byte[] data = readInputStream(jis);
					jarArchive.updateArchivedData(name, data);
				}
			}
		}
		return jarArchive;
	}

	@Override
	public void save(JarArchive jarArchive, OutputStream out) throws IOException {
		try (JarOutputStream jOut = new JarOutputStream(out)) {
			for (Map.Entry<String, byte[]> entry : jarArchive.getArchivedData().entrySet()) {
				JarEntry jarEntry = new JarEntry(entry.getKey());
				byte[] data = entry.getValue();
				jOut.putNextEntry(jarEntry);
				jOut.write(data);
			}
		}
	}

}