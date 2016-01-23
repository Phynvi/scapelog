package com.scapelog.client.loader.analyser.impl;

import com.scapelog.agent.util.InjectionUtils;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.api.ClientFeature;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.injection.ClassInjection;
import com.scapelog.client.util.Debug;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Collection;
import java.util.List;

public final class GameMessageAnalyser extends Analyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		for (ClassNode classNode : classNodes) {
			for (MethodNode methodNode : classNode.methods) {
				InstructionSearcher searcher = new InstructionSearcher(methodNode);
				List<AbstractInsnNode[]> patterns = searcher.findPatterns("GETFIELD BIPUSH AALOAD");
				if (patterns.isEmpty()) {
					continue;
				}
				for (AbstractInsnNode[] pattern : patterns) {
					if (pattern[1].getOpcode() != Opcodes.BIPUSH) {
						continue;
					}
					IntInsnNode bipush = (IntInsnNode) pattern[1];
					if (bipush.operand != 99) {
						continue;
					}
					AbstractInsnNode[] loads = new AbstractInsnNode[8];
					String[] types = new String[loads.length];

					List<AbstractInsnNode> aloads = searcher.getAllByOpcode(Opcodes.ALOAD);
					List<AbstractInsnNode> iloads = searcher.getAllByOpcode(Opcodes.ILOAD);
					for (AbstractInsnNode insnNode : aloads) {
						VarInsnNode aload = (VarInsnNode) insnNode;
						int var = aload.var;
						if (var == 0 || var > loads.length) {
							continue;
						}
						loads[var - 1] = aload;
						types[var - 1] = Type.getDescriptor(String.class);
					}
					for (AbstractInsnNode insnNode : iloads) {
						VarInsnNode iload = (VarInsnNode) insnNode;
						int var = iload.var;
						if (var == 0 || var > loads.length) {
							continue;
						}
						loads[var - 1] = iload;
						types[var - 1] = Type.getDescriptor(int.class);
					}
					Debug.println("lobby messages=" + classNode.name + "." + methodNode.name);
					InsnList instructions = InjectionUtils.createEventInjection(ClientFeature.GAME_MESSAGES.getIdentifier(), loads, types);
					InjectionUtils.inject(methodNode, null, instructions);
					operation.addInjection(classNode.name, new ClassInjection(new MethodInfo(classNode.name, methodNode.name, methodNode.desc), 0, instructions, ClientFeature.GAME_MESSAGES));
				}
			}
		}
	}

}