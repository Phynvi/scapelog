package com.scapelog.client.loader.analyser.injection;

import com.google.common.collect.ImmutableList;
import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.api.ClientFeature;
import com.scapelog.client.ClientFeatures;
import com.scapelog.client.util.Debug;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public final class ClassInjection extends Injection {

	private final MethodInfo methodInfo;

	private final int index;

	private final InsnList instructions;

	private final ImmutableList<ClientFeature> features;

	public ClassInjection(MethodInfo methodInfo, int index, InsnList instructions, ClientFeature... features) {
		this.methodInfo = methodInfo;
		this.index = index;
		this.instructions = instructions;
		this.features = features == null ? ImmutableList.of() : ImmutableList.copyOf(features);
	}

	@Override
	public boolean execute(ClassNode classNode) {
		MethodNode method = ClassNodeUtils.getMethod(classNode, methodInfo);
		if (method == null) {
			return false;
		}
		if (index == -2) {
			method.instructions.insert(instructions);
		} else {
			AbstractInsnNode indexNode = method.instructions.get(index);
			if (indexNode == null) {
				Debug.println("no index found!!");
				return false;
			}
			method.instructions.insert(indexNode, instructions);
			if (features != null) {
				features.forEach(ClientFeatures::enable);
			}
		}
		return true;
	}

}