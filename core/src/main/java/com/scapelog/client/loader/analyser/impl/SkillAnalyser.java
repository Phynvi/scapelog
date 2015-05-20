package com.scapelog.client.loader.analyser.impl;

import com.scapelog.agent.util.InjectionUtils;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.api.ClientFeature;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.ClassInjection;
import com.scapelog.client.util.Tuple;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Collection;
import java.util.List;

public final class SkillAnalyser extends Analyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		for (ClassNode node : classNodes) {
			for (MethodNode methodNode : node.methods) {
				InstructionSearcher searcher = new InstructionSearcher(methodNode);
				List<AbstractInsnNode[]> patterns = searcher.findPatterns("GETSTATIC GETFIELD ILOAD AALOAD ILOAD * INVOKEVIRTUAL");
				if (patterns.isEmpty()) {
					continue;
				}
				if (patterns.size() > 2) {
					continue;
				}
				AbstractInsnNode[] xpPattern = patterns.get(0);
				AbstractInsnNode[] levelPattern = patterns.get(1);
				AbstractInsnNode first = levelPattern[xpPattern.length - 1];

				VarInsnNode skillNode = (VarInsnNode) xpPattern[2];
				VarInsnNode xpNode = (VarInsnNode) xpPattern[4];
				VarInsnNode levelNode = (VarInsnNode) levelPattern[4];

				String key = "packet_parser_" + node.name + "_" + (int) (Math.random() * 10000);
				operation.setAttribute(key, new Tuple<>(node, methodNode));
//				Debug.println("%s=%s.%s [%s]", key, node.name, methodNode.name, methodNode.desc);

				InsnList instructions = InjectionUtils.createEventInjection(ClientFeature.SKILLS.getIdentifier(), new AbstractInsnNode[]{skillNode, levelNode, xpNode});
//				Debug.println("\tinject: %d instructions to %s.%s @ %d", instructions.size(), node.name, methodNode.name, methodNode.instructions.indexOf(first));
				InjectionUtils.inject(methodNode, first, instructions);
				operation.addInjection(node.name, new ClassInjection(new MethodInfo(node.name, methodNode.name, methodNode.desc), methodNode.instructions.indexOf(first), instructions, ClientFeature.SKILLS));
			}
		}
	}

}