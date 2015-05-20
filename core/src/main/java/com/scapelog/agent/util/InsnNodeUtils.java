package com.scapelog.agent.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.Collection;

/**
 * Several utility methods related to instruction nodes.
 * @author Graham Edgecombe
 */
public final class InsnNodeUtils implements Opcodes {

	/**
	 * Creates a numeric push instruction.
	 * @param num The number to push.
	 * @return The instruction node.
	 */
	public static AbstractInsnNode createNumericPushInsn(Number num) {
		long value = num.longValue();
		if (value == -1) {
			return new InsnNode(ICONST_M1);
		} else if (value == 0) {
			return new InsnNode(ICONST_0);
		} else if (value == 1) {
			return new InsnNode(ICONST_1);
		} else if (value == 2) {
			return new InsnNode(ICONST_2);
		} else if (value == 3) {
			return new InsnNode(ICONST_3);
		} else if (value == 4) {
			return new InsnNode(ICONST_4);
		} else if (value == 5) {
			return new InsnNode(ICONST_5);
		} else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
			return new IntInsnNode(BIPUSH, (int) value);
		} else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
			return new IntInsnNode(SIPUSH, (int) value);
		} else if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
			return new LdcInsnNode((int) value);
		} else {
			return new LdcInsnNode(/*(long)*/ value);
		}
	}

	/**
	 * Reads the value of a numeric push instruction (which can be an
	 * {@code ICONST_*} instruction, an {@code BIPUSH} instruction, an
	 * {@code SIPUSH} instruction or a {@code LDC_*} instruction.
	 * @param push The instruction node.
	 * @return The numeric value.
	 */
	public static long getNumericPushValue(AbstractInsnNode push) {
		if (push instanceof InsnNode) {
			switch (push.getOpcode()) {
			case ICONST_M1:
				return -1;
			case ICONST_0:
				return 0;
			case ICONST_1:
				return 1;
			case ICONST_2:
				return 2;
			case ICONST_3:
				return 3;
			case ICONST_4:
				return 4;
			case ICONST_5:
				return 5;
			default:
				throw new AssertionError();
			}
		} else if (push instanceof IntInsnNode) {
			return ((IntInsnNode) push).operand;
		} else {
			return ((Number) ((LdcInsnNode) push).cst).longValue();
		}
	}

	/**
	 * Finds the next non-psuedo node following the specified node.
	 * @param node The node.
	 * @return The next non-psuedo node, or {@code null} if the end of the
	 * instruction list is reached.
	 */
	public static AbstractInsnNode nextNonPsuedoNode(AbstractInsnNode node) {
		while ((node = node.getNext()) != null && node.getOpcode() == -1);
		return node;
	}

	/**
	 * Finds the previous non-psuedo node following the specified node.
	 * @param node The node.
	 * @return The previous non-psuedo node, or {@code null} if the start of
	 * the instruction list is reached.
	 */
	public static AbstractInsnNode previousNonPsuedoNode(AbstractInsnNode node) {
		while ((node = node.getPrevious()) != null && node.getOpcode() == -1);
		return node;
	}

	/**
	 * Finds the next psuedo node following the specified node.
	 * @param node The node.
	 * @return The next psuedo node, or {@code null} if the end of the
	 * instruction list is reached.
	 */
	public static AbstractInsnNode nextPsuedoNode(AbstractInsnNode node) {
		while ((node = node.getNext()) != null && node.getOpcode() != -1);
		return node;
	}
	/**
	 * Finds the previous psuedo node following the specified node.
	 * @param node The node.
	 * @return The previous psuedo node, or {@code null} if the start of the
	 * instruction list is reached.
	 */
	public static AbstractInsnNode previousPsuedoNode(AbstractInsnNode node) {
		while ((node = node.getPrevious()) != null && node.getOpcode() != -1);
		return node;
	}

    public static AbstractInsnNode valueOf(AbstractInsnNode node) {
        int opcode = node.getOpcode();
        int type = -1;
        switch (opcode) {
            case ILOAD:
                type = Type.INT;
                break;
            case DLOAD:
                type = Type.DOUBLE;
                break;
            case LLOAD:
                type = Type.LONG;
                break;
            case GETFIELD:
            case GETSTATIC:
                FieldInsnNode field = (FieldInsnNode) node;
                switch(field.desc) {
                    case "I":
                        type = Type.INT;
                        break;
                    case "D":
                        type = Type.DOUBLE;
                        break;
                    case "J":
                        type = Type.LONG;
                        break;
                }
                break;
        }
        return valueOf(type);
    }

	public static String getType(AbstractInsnNode node) {
		int opcode = node.getOpcode();
		String type = null;
		switch (opcode) {
			case ILOAD:
				type = Type.getDescriptor(int.class);
				break;
			case DLOAD:
				type = Type.getDescriptor(double.class);
				break;
			case LLOAD:
				type = Type.getDescriptor(long.class);
				break;
			case LDC:
				type = Type.getDescriptor(String.class);
				break;
			default:
				System.err.println("trying to get type of unknown opcode " + node.getOpcode() + ", type=" + node.getType());
				break;
		}
		return type;
	}

    public static AbstractInsnNode valueOf(int type) {
        switch(type) {
            case Type.INT: //int
                return new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            case Type.DOUBLE: //double
                return new MethodInsnNode(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            case Type.LONG: //long
                return new MethodInsnNode(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        }
        return null;
    }

    public static boolean fieldsEquals(FieldInsnNode field1, FieldInsnNode field2) {
        return field1.owner.equals(field2.owner) && field1.name.equals(field2.name) && field1.desc.equals(field2.desc);
    }

    public static String toString(AbstractInsnNode ins) {
        if(ins instanceof FieldInsnNode) {
            FieldInsnNode field = (FieldInsnNode) ins;
            return field.owner + "." + field.name + " (" + field.desc + ")";
        }
        return null;
    }

	public static boolean isPublic(Collection<ClassNode> classNodes, FieldInsnNode fieldInsnNode) {
		ClassNode owner = ClassNodeUtils.getClassNode(classNodes, fieldInsnNode.owner);
		if (owner == null) {
			return false;
		}
		FieldNode field = ClassNodeUtils.getField(owner, fieldInsnNode.name, fieldInsnNode.desc);
		if (field == null) {
			return false;
		}
		return (field.access & Opcodes.ACC_PRIVATE) == 0 || (field.access & Opcodes.ACC_FINAL) == 0;
	}

	/**
	 * Default private constructor to prevent instantiation.
	 */
	private InsnNodeUtils() {

	}

}