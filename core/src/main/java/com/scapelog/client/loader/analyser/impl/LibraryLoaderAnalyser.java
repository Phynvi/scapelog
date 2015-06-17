package com.scapelog.client.loader.analyser.impl;

import com.scapelog.agent.util.InjectionUtils;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.client.loader.LibraryModifier;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.ClassInjection;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Collection;
import java.util.List;

public final class LibraryLoaderAnalyser extends Analyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		for (ClassNode classNode : classNodes) {
			for (MethodNode methodNode : classNode.methods) {
				if (!methodNode.desc.endsWith(")I")) {
					continue;
				}
				InstructionSearcher searcher = new InstructionSearcher(methodNode);

				List<AbstractInsnNode[]> patterns = searcher.findPatterns("INVOKEVIRTUAL IFEQ BIPUSH IRETURN");
				if (patterns.isEmpty()) {
					continue;
				}
				for (AbstractInsnNode[] pattern : patterns) {
					MethodInsnNode methodInsnNode = (MethodInsnNode) pattern[0];
					IntInsnNode bipush = (IntInsnNode) pattern[2];
					if (!methodInsnNode.name.equals("containsKey")) {
						continue;
					}
					if (bipush.operand != 100) {
						continue;
					}
					VarInsnNode nameAload = null;
					VarInsnNode payloadAload = null;
					AbstractInsnNode injectionIndex = null;

					int index = searcher.getIndex();
					searcher.resetIndex();

					while (searcher.next(Opcodes.INVOKESTATIC) != null) {
						MethodInsnNode current = (MethodInsnNode) searcher.current();
						if (!current.desc.endsWith("Ljava/io/File;")) {
							continue;
						}
						AbstractInsnNode next = searcher.right();
						if (next.getOpcode() == Opcodes.ASTORE) {
							int idx = searcher.getIndex();

							injectionIndex = next;
							nameAload = (VarInsnNode) searcher.previous(Opcodes.ALOAD);

							searcher.setIndex(idx);
						}
					}
					if (nameAload == null) {
						continue;
					}
					searcher.resetIndex();
					while (searcher.next(Opcodes.INVOKEVIRTUAL) != null) {
						MethodInsnNode current = (MethodInsnNode) searcher.current();
						if (!current.desc.endsWith(")[B")) {
							continue;
						}
						payloadAload = getPayloadAload(searcher);
					}
					if (payloadAload == null) {
						continue;
					}
					InsnList instructions = new InsnList();
					instructions.add(new VarInsnNode(Opcodes.ALOAD, nameAload.var));
					instructions.add(new VarInsnNode(Opcodes.ALOAD, payloadAload.var));
					instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(LibraryModifier.class), "modifyLibrary", Type.getMethodDescriptor(Type.getType(byte[].class), Type.getType(String.class), Type.getType(byte[].class)), false));
					instructions.add(new VarInsnNode(Opcodes.ASTORE, payloadAload.var));
					InjectionUtils.inject(methodNode, injectionIndex, instructions);
					operation.addInjection(classNode.name, new ClassInjection(new MethodInfo(classNode.name, methodNode.name, methodNode.desc), methodNode.instructions.indexOf(injectionIndex), instructions, null));

					searcher.setIndex(index);
				}
			}
		}
	}

	private VarInsnNode getPayloadAload(InstructionSearcher searcher) {
		AbstractInsnNode next = searcher.right();
		if (next.getOpcode() == Opcodes.ASTORE) {
			return (VarInsnNode) next;
		}
		return null;
	}

}