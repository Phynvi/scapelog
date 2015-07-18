package com.scapelog.client.loader.analyser.impl;

import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.agent.util.InjectionUtils;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.client.ClassStore;
import com.scapelog.client.loader.AppletClassLoader;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.injection.ClassInjection;
import com.scapelog.client.loader.analyser.injection.ReplaceInjection;
import com.scapelog.client.util.Debug;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Hashtable;

public final class ClassLoaderAnalyser extends Analyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		for (ClassNode classNode : classNodes) {
			for (MethodNode methodNode : classNode.methods) {
				InstructionSearcher searcher = new InstructionSearcher(methodNode);
				while (searcher.next(MethodInsnNode.class, instr -> true) != null) {
					MethodInsnNode methodInsnNode = searcher.current();
					if (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL
							&& methodInsnNode.name.equals("defineClass")
							&& methodInsnNode.desc.equals("(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;")) {
						MethodInsnNode newMethodInsnNode = new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(AppletClassLoader.class), "__defineClass", Type.getMethodDescriptor(Type.getType(Class.class), Type.getType(String.class), Type.getType(byte[].class), Type.getType(int.class), Type.getType(int.class), Type.getType(ProtectionDomain.class), Type.getType(ClassLoader.class)), false);

						InsnList instructions = new InsnList();
						instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));

						int index = methodNode.instructions.indexOf(methodInsnNode) - 1;
						MethodInfo methodInfo = new MethodInfo(classNode.name, methodNode.name, methodNode.desc);
						ClassInjection injection = new ClassInjection(methodInfo, index, instructions);
						operation.addInjection(classNode.name, injection);
						InjectionUtils.inject(methodNode, index, instructions);

						methodInfo = new MethodInfo(classNode.name, methodNode.name, methodNode.desc);
						index = methodNode.instructions.indexOf(methodInsnNode);
						operation.addInjection(classNode.name, new ReplaceInjection(methodInfo, index, newMethodInsnNode));
						methodNode.instructions.set(methodInsnNode, newMethodInsnNode);
						Debug.println("defineClass modified at %s.%s", classNode.name, methodNode.name);
						break;
					}
				}
			}


			if (!classNode.superName.contains("ClassLoader")) {
				continue;
			}
			for (MethodNode methodNode : classNode.methods) {
				if (methodNode.name.equals("findClass")) {
					boolean found = findMapPut(classNode, methodNode, operation);
					if (found) {
						break;
					}
					InstructionSearcher searcher = new InstructionSearcher(methodNode);
					while (searcher.next(Opcodes.INVOKESTATIC) != null) {
						MethodInsnNode findClass = searcher.current();
						if (!findClass.desc.endsWith("Ljava/lang/Class;")) {
							continue;
						}

						ClassNode node = ClassNodeUtils.getClassNode(classNodes, findClass.owner);
						if (node == null) {
							continue;
						}
						MethodNode method = ClassNodeUtils.getMethod(node, findClass.name, findClass.desc);
						if (method == null) {
							continue;
						}
						findMapPut(node, method, operation);
					}
				}
				if (methodNode.name.equals("<init>")) {
					MethodInsnNode methodInsnNode = (MethodInsnNode) methodNode.instructions.get(1);
					MethodInsnNode newMethodInsnNode = new MethodInsnNode(methodInsnNode.getOpcode(), methodInsnNode.owner, methodInsnNode.name, Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(ClassLoader.class)), false);

					InsnList instructions = new InsnList();
					instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(ClassLoader.class), "getSystemClassLoader", Type.getMethodDescriptor(Type.getType(ClassLoader.class)), false));

					int index = 0;
					MethodInfo methodInfo = new MethodInfo(classNode.name, methodNode.name, methodNode.desc);
					ClassInjection injection = new ClassInjection(methodInfo, index, instructions);
					operation.addInjection(classNode.name, injection);
					InjectionUtils.inject(methodNode, index, instructions);

					methodInfo = new MethodInfo(classNode.name, methodNode.name, methodNode.desc);
					index = methodNode.instructions.indexOf(methodInsnNode);
					operation.addInjection(classNode.name, new ReplaceInjection(methodInfo, index, newMethodInsnNode));
					methodNode.instructions.set(methodInsnNode, newMethodInsnNode);
				}
			}
		}
	}

	private boolean findMapPut(ClassNode classNode, MethodNode methodNode, AnalysingOperation operation) {
		InstructionSearcher searcher = new InstructionSearcher(methodNode);
		MethodInsnNode putInstruction;
		while((putInstruction = (MethodInsnNode) searcher.next(Opcodes.INVOKEVIRTUAL)) != null) {
			if (putInstruction.name.equals("put") && putInstruction.owner.equals(Type.getInternalName(Hashtable.class))) {
				int index = searcher.getIndex();

				VarInsnNode classObjectInstruction = (VarInsnNode) searcher.previous(Opcodes.ALOAD);
				VarInsnNode classNameInstruction = (VarInsnNode) searcher.previous(Opcodes.ALOAD);

				MethodInfo methodInfo = new MethodInfo(classNode.name, methodNode.name, methodNode.desc);
				InsnList instructions = getInjection(classObjectInstruction, classNameInstruction);
				ClassInjection injection = new ClassInjection(methodInfo, index, instructions);
				operation.addInjection(classNode.name, injection);
				InjectionUtils.inject(methodNode, index, instructions);

				searcher.setIndex(index);
				Debug.println("ClassLoader modified at %s.%s", classNode.name, methodNode.name);
				return true;
			}
		}
		return false;
	}

	private InsnList getInjection(VarInsnNode classObjectInstruction, VarInsnNode classNameInstruction) {
		InsnList injections = new InsnList();
		injections.add(classNameInstruction.clone(null));
		injections.add(classObjectInstruction.clone(null));
		injections.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(ClassStore.class), "addClass", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.getType(Object.class)), false));
		return injections;
	}

}