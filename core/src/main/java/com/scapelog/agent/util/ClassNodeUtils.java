package com.scapelog.agent.util;

import com.scapelog.client.ScapeLog;
import com.scapelog.client.util.Debug;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public final class ClassNodeUtils {

	private ClassNodeUtils() {

	}

	public static void dumpClass(ClassNode node) {
		if (ScapeLog.debug) {
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			node.accept(writer);
			dumpClass(node.name, writer.toByteArray());
		}
	}

	public static void dumpClass(String name, byte[] bytes) {
		if (ScapeLog.debug) {
			if (name.contains("/")) {
				return;
			}
			try {
				Path destination = Paths.get("/tmp/classes");
				if (Files.notExists(destination)) {
					Files.createDirectory(destination);
				}

				Files.write(destination.resolve(name + ".class"), bytes);
				Debug.println("dumped %s", name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ClassNode getClassNode(Collection<ClassNode> classNodes, String name) {
		for (ClassNode classNode : classNodes) {
			if (classNode.name.equals(name)) {
				return classNode;
			}
		}
		return null;
	}

	public static MethodNode getMethod(ClassNode node, String name, String desc) {
		for (MethodNode methodNode : node.methods) {
			if (methodNode.name.equals(name) && methodNode.desc.equals(desc)) {
				return methodNode;
			}
		}
		return null;
	}

	public static MethodNode getMethod(ClassNode node, String name) {
		if (node == null) {
			return null;
		}
		for (MethodNode methodNode : node.methods) {
			if (methodNode.name.equals(name)) {
				return methodNode;
			}
		}
		return null;
	}

	public static FieldNode getField(ClassNode node, String name, String desc) {
		for (FieldNode fieldNode : node.fields) {
			if (fieldNode.name.equals(name) && fieldNode.desc.equals(desc)) {
				return fieldNode;
			}
		}
		return null;
	}

	public static FieldNode getField(ClassNode node, String name) {
		if (node == null) {
			return null;
		}
		for (FieldNode fieldNode : node.fields) {
			if (fieldNode.name.equals(name)) {
				return fieldNode;
			}
		}
		return null;
	}

}