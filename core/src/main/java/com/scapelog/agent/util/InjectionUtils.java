package com.scapelog.agent.util;

import com.scapelog.client.event.ClientEventReceiver;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public final class InjectionUtils implements Opcodes {

	public static final String SEPARATOR = "<<<>>>";

	public static InsnList createEventInjection(String identifier, AbstractInsnNode[] nodes, String... types) {
		InstructionGroup[] groups = new InstructionGroup[nodes.length];
		for (int i = 0; i < groups.length; i++) {
			String type = (i >= 0 && i < types.length) ? types[i] : null;
			groups[i] = new InstructionGroup(type, nodes[i]);
		}
		return createEventInjection(identifier, groups);
	}

	public static InsnList createEventInjection(String identifier, InstructionGroup... groups) {
		InsnList list = new InsnList();
		try {
			String stringBuilderName = Type.getInternalName(StringBuilder.class);

			list.add(new TypeInsnNode(NEW, Type.getInternalName(StringBuilder.class)));
			list.add(new InsnNode(DUP));
			list.add(new MethodInsnNode(INVOKESPECIAL, stringBuilderName, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE), false));
			list.add(new LdcInsnNode(identifier + SEPARATOR));
			list.add(new MethodInsnNode(INVOKEVIRTUAL, stringBuilderName, "append", Type.getMethodDescriptor(Type.getType(StringBuilder.class), Type.getType(String.class)), false));
			for (int i = 0; i < (groups == null ? 0 : groups.length); i++) {
				InstructionGroup group = groups[i];
				String type = group.getType();
				AbstractInsnNode[] instructions = group.getInstructions();

				for (AbstractInsnNode instruction : instructions) {
					list.add(instruction.clone(null));
				}
				Type.getMethodDescriptor(Type.getType(StringBuilder.class), Type.getType(type == null ? InsnNodeUtils.getType(instructions[instructions.length - 1]) : type));
				list.add(new MethodInsnNode(INVOKEVIRTUAL,
						Type.getInternalName(StringBuilder.class),
						"append",
						//"(" + (type == null ? InsnNodeUtils.getType(instructions[instructions.length - 1]) : type) + ")Ljava/lang/StringBuilder;",
						Type.getMethodDescriptor(Type.getType(StringBuilder.class), Type.getType(type == null ? InsnNodeUtils.getType(instructions[instructions.length - 1]) : type)),
						false));
				list.add(new LdcInsnNode(SEPARATOR));
				list.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(StringBuilder.class), "append", Type.getMethodDescriptor(Type.getType(StringBuilder.class), Type.getType(String.class)), false));
			}
			list.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(StringBuilder.class), "toString", Type.getMethodDescriptor(Type.getType(String.class)), false));
			list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(ClientEventReceiver.class), "receive", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class)), false));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void inject(MethodNode method, int index, InsnList instructions) {
		AbstractInsnNode indexNode = method.instructions.get(index);
		inject(method, indexNode, instructions);
	}

	public static void inject(MethodNode method, AbstractInsnNode indexNode, InsnList instructions) {
		InsnList copiedList = new InsnList();
		for (int i = 0; i < instructions.size(); i++) {
			AbstractInsnNode node = instructions.get(i);
			if (node.getClass().equals(LabelNode.class) || node.getClass().equals(JumpInsnNode.class)) {
				copiedList.add(node);
			} else {
				copiedList.add(node.clone(null));
			}
		}
		if (indexNode == null) {
			method.instructions.insert(copiedList);
		} else {
			method.instructions.insert(indexNode, copiedList);
		}
	}

}