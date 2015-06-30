package com.scapelog.client.loader.analyser.impl;

import com.google.common.collect.Lists;
import com.scapelog.agent.util.InjectionUtils;
import com.scapelog.agent.util.InstructionGroup;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.api.ClientFeature;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.injection.ClassInjection;
import com.scapelog.client.util.Debug;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.List;

public final class VariableAnalyser extends Analyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		for (ClassNode node : classNodes) {
			List<MethodInfo> updateVarps = Lists.newArrayList();

			for (MethodNode method : node.methods) {
				InstructionSearcher searcher = new InstructionSearcher(method);
				if (searcher.nextLDC(4611686018427387905L) == null) {
					continue;
				}
				if (searcher.nextLDC(4611686018427387905L) == null) {
					continue;
				}
				Debug.println("potential update_varp=%s.%s:%s", node.name, method.name, method.desc);

				String[] instructionPatterns = {
						"ALOAD GETFIELD LDC IMUL ILOAD",
						"LDC ALOAD GETFIELD IMUL ILOAD"
				};
				for (String instructionPattern : instructionPatterns) {
					List<AbstractInsnNode[]> patterns = searcher.findPatterns(instructionPattern);
					for (AbstractInsnNode[] pattern : patterns) {
						Debug.println("\tvalid!");
						InstructionGroup firstArgument = new InstructionGroup(Type.getDescriptor(int.class), pattern[0], pattern[1], pattern[2], pattern[3]);
						InstructionGroup secondArgument = new InstructionGroup(Type.getDescriptor(int.class), pattern[4]);

						updateVarps.add(new MethodInfo(node.name, method.name, method.desc));

						InsnList instructions = InjectionUtils.createEventInjection(ClientFeature.VARIABLES.getIdentifier(), firstArgument, secondArgument);
						InjectionUtils.inject(method, null, instructions);
						operation.addInjection(node.name, new ClassInjection(new MethodInfo(node.name, method.name, method.desc), 0, instructions, ClientFeature.VARIABLES));
					}
				}
			}
		}
	}

}