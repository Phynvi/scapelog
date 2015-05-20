package com.scapelog.client.loader.archive;

import org.objectweb.asm.tree.ClassNode;

import java.util.Map.Entry;

/**
 * @author Sean
 */
public final class ClassNodeArchive extends Archive<ClassNode> {

	/**
	 * The {@link JarArchive}.
	 */
	private final JarArchive archive;

	/**
	 * Creates a new {@link ClassNode} archive.
	 * @param archive The {@link JarArchive}.
	 */
	public ClassNodeArchive(JarArchive archive) {
		this.archive = archive;
	}

	/**
	 * Converts the {@link Class}s in the {@link JarArchive} into {@link ClassNode}s.
	 */
	public void addClassNodes() {
		for (Entry<String, byte[]> entry : archive.getArchivedData().entrySet()) {
			ClassNode node = archive.toClassNode(entry.getKey());
//			ClassNodeUtils.dumpClass(node);
			if(node != null) {
				updateArchivedData(entry.getKey(), node);
			}
		}
	}

	/**
	 * Gets the {@link JarArchive}.
	 * @return the archive.
	 */
	public JarArchive getArchive() {
		return archive;
	}

}