package com.scapelog.client.loader.analyser.impl;

import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.client.ClassStore;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.ClassInjection;
import com.scapelog.client.util.Debug;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Collection;
import java.util.Hashtable;

public final class ClassLoaderAnalyser extends Analyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		for (ClassNode classNode : classNodes) {
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
						MethodInsnNode findClass = (MethodInsnNode) searcher.current();
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
				ClassInjection injection = new ClassInjection(methodInfo, index, getInjection(classObjectInstruction, classNameInstruction));
				operation.addInjection(classNode.name, injection);

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