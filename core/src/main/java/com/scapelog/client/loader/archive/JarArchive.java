package com.scapelog.client.loader.archive;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Sean
 */
public final class JarArchive extends Archive<byte[]> {

	/**
	 * Returns the data in the archive as a {@link ClassNode}.
	 * @param name The name of the archived piece of data.
	 * @return The {@link ClassNode}.
	 */
	public ClassNode toClassNode(String name) {
		if (name.endsWith(".class")) {
			byte[] data = get(name);
			ClassNode node = new ClassNode();
			ClassReader reader = new ClassReader(data);
			reader.accept(node, ClassReader.EXPAND_FRAMES | ClassReader.SKIP_DEBUG);
			return node;
		}
		return null;
	}

}
