package com.scapelog.client.loader.analyser.injection;

import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.agent.util.tree.MethodInfo;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public final class ReplaceInjection extends Injection {

	private final MethodInfo methodInfo;

	private final int index;

	private final AbstractInsnNode newInstruction;

	public ReplaceInjection(MethodInfo methodInfo, int index, AbstractInsnNode newInstruction) {
		this.methodInfo = methodInfo;
		this.index = index;
		this.newInstruction = newInstruction;
	}

	@Override
	public boolean execute(ClassNode classNode) {
		MethodNode methodNode = ClassNodeUtils.getMethod(classNode, methodInfo);
		if (methodNode == null) {
			return false;
		}
		InsnList instructions = methodNode.instructions;
		if (index < 0 || index > instructions.size()) {
			return false;
		}
		AbstractInsnNode instruction = instructions.get(index);
		if (instruction == null) {
			return false;
		}
		instructions.set(instruction, newInstruction);
		return true;
	}

}