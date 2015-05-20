package com.scapelog.agent;

import com.google.common.collect.Maps;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.api.ClientFeature;
import com.scapelog.client.ClientFeatures;
import com.scapelog.client.loader.analyser.ClassInjection;
import com.scapelog.client.util.Debug;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;

public final class RSClassTransformer implements ClassFileTransformer {
	private static Map<String, List<ClassInjection>> injections = Maps.newHashMap();

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
			if (injections != null && !injections.isEmpty()) {
				List<ClassInjection> classInjections = injections.get(classNode.name);
				if (classInjections == null) {
					return classfileBuffer;
				}
				for (ClassInjection injection : classInjections) {
					InsnList instructions = injection.getInstructions();
					MethodNode method = getMethod(classNode, injection.getMethodInfo());
					if (method == null) {
						continue;
					}
					int index = injection.getIndex();
					if (index == -2) {
						method.instructions.insert(instructions);
					} else {
						AbstractInsnNode indexNode = method.instructions.get(index);
						if (indexNode == null) {
							Debug.println("no index found!!");
							continue;
						}
						method.instructions.insert(indexNode, instructions);
						if (injection.getFeatures() != null) {
							for (ClientFeature feature : injection.getFeatures()) {
								ClientFeatures.enable(feature);
							}
						}
					}
					transformed = true;
				}
			}

//			ClassNodeUtils.dumpClass(classNode);

			if (transformed) {
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

	private MethodNode getMethod(ClassNode classNode, MethodInfo methodInfo) {
		for (MethodNode methodNode : classNode.methods) {
			if (methodNode.name.equals(methodInfo.getName()) && methodNode.desc.equals(methodInfo.getDescription())) {
				return methodNode;
			}
		}
		return null;
	}

	public static void addInjections(Map<String, List<ClassInjection>> injections) {
		RSClassTransformer.injections.putAll(injections);
	}
}
