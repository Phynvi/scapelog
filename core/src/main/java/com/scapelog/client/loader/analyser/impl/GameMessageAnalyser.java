package com.scapelog.client.loader.analyser.impl;

import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.agent.util.InjectionUtils;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.api.ClientFeature;
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

import java.util.Collection;

public final class GameMessageAnalyser extends Analyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		for (ClassNode node : classNodes) {
			try {
				for (MethodNode methodNode : node.methods) {
					InstructionSearcher searcher = new InstructionSearcher(methodNode);
					while (searcher.next() != null) {
						AbstractInsnNode current = searcher.current();
						if (!(current instanceof IntInsnNode)) {
							continue;
						}
						IntInsnNode var = (IntInsnNode) current;
						if (var.operand != 99) {
							continue;
						}
						var = (IntInsnNode) searcher.next(Opcodes.BIPUSH);
						if (var == null) {
							continue;
						}
						if (var.operand != 98) {
							continue;
						}
						MethodInsnNode methodCall = null;
						while (searcher.next(Opcodes.INVOKESTATIC) != null) {
							MethodInsnNode methodInsnNode = (MethodInsnNode) searcher.current();
							if (methodInsnNode.desc.endsWith(")V") && methodInsnNode.desc.contains("II")) {
								methodCall = methodInsnNode;
								break;
							}
						}
						AbstractInsnNode[] loads = new AbstractInsnNode[6];
						String[] types = new String[loads.length];
						int idx = 0;
						if (methodCall != null) {
							while (idx < loads.length && searcher.previous() != null) {
								AbstractInsnNode currentLoad = searcher.current();
								int opcode = currentLoad.getOpcode();
								if (opcode == Opcodes.ILOAD || opcode == Opcodes.ALOAD) {
									loads[idx] = searcher.current();
									if (opcode == Opcodes.ALOAD) {
										types[idx] = Type.getDescriptor(String.class);
									}
									idx++;
								}
							}
							InsnList instructions = InjectionUtils.createEventInjection(ClientFeature.GAME_MESSAGES.getIdentifier(), loads, types);
							InjectionUtils.inject(methodNode, methodCall, instructions);
							operation.addInjection(node.name, new ClassInjection(new MethodInfo(node.name, methodNode.name, methodNode.desc), methodNode.instructions.indexOf(methodCall), instructions, ClientFeature.GAME_MESSAGES));
						}
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}