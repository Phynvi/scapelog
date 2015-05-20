package com.scapelog.agent.util;

import org.objectweb.asm.tree.AbstractInsnNode;

public final class InstructionGroup {

	private final AbstractInsnNode[] instructions;

	private final String type;

	public InstructionGroup(AbstractInsnNode... instructions) {
		this(null, instructions);
	}

	public InstructionGroup(String type, AbstractInsnNode... instructions) {
		this.instructions = instructions;
		this.type = type;
	}

	public AbstractInsnNode[] getInstructions() {
		return instructions;
	}

	public String getType() {
		return type;
	}

}