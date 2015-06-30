package com.scapelog.agent;

import com.google.common.collect.Maps;
import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.client.loader.analyser.injection.Injection;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;

public final class RSClassTransformer implements ClassFileTransformer {
	private static Map<String, List<Injection>> injections = Maps.newHashMap();

	@Override
	public byte[] transform(ClassLoader loader, String fullyQualifiedClassName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		try {
			if (fullyQualifiedClassName == null || fullyQualifiedClassName.contains("/")) {
				return classfileBuffer;
			}

			ClassReader reader = new ClassReader(classfileBuffer);
			ClassNode classNode = new ClassNode();
			reader.accept(classNode, ClassReader.EXPAND_FRAMES | ClassReader.SKIP_DEBUG);

			boolean transformed = false;
			try {
				if (injections != null && !injections.isEmpty()) {
					List<Injection> injections = RSClassTransformer.injections.get(classNode.name);
					if (injections == null) {
						return classfileBuffer;
					}
					for (Injection injection : injections) {
						transformed = injection.execute(classNode);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (transformed) {
				try {
					ClassNodeUtils.dumpClass(classNode);

					ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
					classNode.accept(writer);
					return writer.toByteArray();
				} catch (Exception e) {
					return classfileBuffer;
				}
			}
		} catch (Exception e) {
			return classfileBuffer;
		}
		return classfileBuffer;
	}

	public static void addInjections(Map<String, List<Injection>> injections) {
		RSClassTransformer.injections.putAll(injections);
	}
}
