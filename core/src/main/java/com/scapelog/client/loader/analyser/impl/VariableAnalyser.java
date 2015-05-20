package com.scapelog.client.loader.analyser.impl;

import com.google.common.collect.Lists;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.util.Debug;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.List;

public final class VariableAnalyser extends Analyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		List<MethodInfo> updateVarps = Lists.newArrayList();

		for (ClassNode node : classNodes) {
			for (MethodNode method : node.methods) {
				InstructionSearcher searcher = new InstructionSearcher(method);
				if (searcher.nextLDC(4611686018427387905L) == null) {
					continue;
				}
				if (searcher.nextLDC(4611686018427387905L) == null) {
					continue;
				}
				updateVarps.add(new MethodInfo(node.name, method.name, method.desc));
				Debug.println("update_varp=%s.%s", node.name, method.name);

				/*List<AbstractInsnNode[]> patterns = searcher.findPatterns("LDC ALOAD GETFIELD IMUL ILOAD");
				for (AbstractInsnNode[] pattern : patterns) {
					InstructionGroup firstArgument = new InstructionGroup(Type.getDescriptor(int.class), pattern[0], pattern[1], pattern[2], pattern[3]);
					InstructionGroup secondArgument = new InstructionGroup(Type.getDescriptor(int.class), pattern[4]);

					InsnList instructions = InjectionUtils.createPrintInjection(ClientFeature.VARIABLES.getIdentifier(), firstArgument, secondArgument);
					InjectionUtils.inject(method, null, instructions);
					operation.addInjection(node.name, new ClassInjection(new MethodInfo(node.name, method.name, method.desc), 0, instructions, ClientFeature.VARIABLES));
				}*/
			}
		}

/*		List<Tuple<ClassNode, MethodNode>> packetParsers = operation.getAttribute("packet_parser_*");
		for (Tuple<ClassNode, MethodNode> packetParser : packetParsers) {
			ClassNode node = packetParser.getKey();
			MethodNode method = packetParser.getValue();
			InstructionSearcher searcher = new InstructionSearcher(method);

			while(searcher.next(Opcodes.INVOKEVIRTUAL) != null) {
				MethodInsnNode call = (MethodInsnNode) searcher.current();

				updateVarps.stream().filter(methodInfo -> call.owner.equals(methodInfo.getOwnerName()) && call.name.equals(methodInfo.getMethodName()) && call.desc.equals(methodInfo.getDescription())).forEach(methodInfo -> {
					VarInsnNode valueVar = (VarInsnNode) searcher.previous(Opcodes.ILOAD);
					VarInsnNode idVar = (VarInsnNode) searcher.previous(Opcodes.ILOAD);

					InsnList instructions = InjectionUtils.createEventInjection(ClientFeature.VARIABLES.getIdentifier(), new AbstractInsnNode[]{
							idVar,
							valueVar
					});
					InjectionUtils.inject(method, call, instructions);
					operation.addInjection(node.name, new ClassInjection(new MethodInfo(node.name, method.name, method.desc), method.instructions.indexOf(call), instructions, ClientFeature.VARIABLES));

					searcher.setIndex(method.instructions.indexOf(call) + 1);
				});
			}
		}*/
	}

}