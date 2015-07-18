package com.scapelog.agent;

import com.google.common.collect.Maps;
import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.client.jagex.jaggl.GL;
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
			if (fullyQualifiedClassName == null || (fullyQualifiedClassName.contains("/") && !fullyQualifiedClassName.contains("jaggl"))) {
				return classfileBuffer;
			}

			ClassReader reader = new ClassReader(classfileBuffer);
			ClassNode classNode = new ClassNode();
			reader.accept(classNode, ClassReader.EXPAND_FRAMES | ClassReader.SKIP_DEBUG);

			boolean transformed = false;

			if (fullyQualifiedClassName.contains("jaggl")) {
				classNode = modifyJaggl(classNode);
				if (classNode == null) {
					return classfileBuffer;
				}
				transformed = true;
			}

			try {
				if (injections != null && !injections.isEmpty()) {
					List<Injection> injections = RSClassTransformer.injections.get(classNode.name);
					if (injections != null) {
						for (Injection injection : injections) {
							try {
								boolean transform = injection.execute(classNode);
								if (!transformed && transform) {
									transformed = true;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (transformed) {
				ClassNodeUtils.dumpClass(classNode);
				try {
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

	private static ClassNode modifyJaggl(ClassNode classNode) {
		if (classNode.name.equals("jaggl/OpenGL")) {
			classNode.interfaces.add(GL.class.getName().replaceAll("\\.", "/"));
			return classNode;
		}
		return null;
	}

	public static void addInjections(Map<String, List<Injection>> injections) {
		RSClassTransformer.injections.putAll(injections);
	}
}
