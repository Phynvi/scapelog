package com.scapelog.client.loader.archive;

import com.scapelog.client.loader.util.StringUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;

/**
 * @author Sean
 */
public final class JarArchiveClassLoader extends ClassLoader {

	/**
	 * The {@link JarArchive} to load a certain class from.
	 */
	private final JarArchive jarArchive;

	/**
	 * Creates a new {@link JarArchiveClassLoader}.
	 * @param jarArchive The {@link JarArchive}.
	 */
	public JarArchiveClassLoader(JarArchive jarArchive) {
		this.jarArchive = jarArchive;
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			byte[] data = jarArchive.get(StringUtils.classToFileName(name));
			Class<?> clazz = defineClass(name, data, 0, data.length, getDomain());
			if (clazz == null) {
				clazz = findSystemClass(name);
			}
			return clazz;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		try {
			 return new ByteArrayInputStream(jarArchive.get(name));
		} catch (IllegalArgumentException e) {
			return ClassLoader.getSystemResourceAsStream(name);
		}
	}

	private ProtectionDomain getDomain() {
		CodeSource code = new CodeSource(null, (Certificate[]) null);
		return new ProtectionDomain(code, getPermissions());
	}

	private Permissions getPermissions() {
		Permissions permissions = new Permissions();
		// todo: permissions
		permissions.add(new AllPermission());
		return permissions;
	}

	public Class<?> nodeToClass(ClassNode node) {
		if (super.findLoadedClass(node.name) != null) {
			return findLoadedClass(node.name);
		}
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		node.accept(cw);
		byte[] b = cw.toByteArray();
		return defineClass(node.name.replace('/', '.'), b, 0, b.length, getDomain()); /// ClassNodes store packages with a "/", but this method expects a "."
	}

	public static JarArchiveClassLoader create(JarArchive archive) {
		return AccessController.doPrivileged(new PrivilegedClassLoaderAction(archive));
	}

	private static class PrivilegedClassLoaderAction implements PrivilegedAction<JarArchiveClassLoader> {

		private final JarArchive archive;

		public PrivilegedClassLoaderAction(JarArchive archive) {
			this.archive = archive;
		}

		@Override
		public JarArchiveClassLoader run() {
			return new JarArchiveClassLoader(archive);
		}
	}

}