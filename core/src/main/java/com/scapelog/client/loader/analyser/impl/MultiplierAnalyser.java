package com.scapelog.client.loader.analyser.impl;

import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.FieldNodeInfo;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Vastico
 */
public final class MultiplierAnalyser extends Analyser {

	private static final Map<FieldNodeInfo, Map<Integer, List<BigInteger>>> pairs = new HashMap<>();
	private static final Map<FieldNodeInfo, Pair> identifiedPairs = new HashMap<>();

	private static final InstructionSearcher.Constraint<AbstractInsnNode> multiplierConstraint = instr -> instr.getOpcode() == Opcodes.IMUL || instr.getOpcode() == Opcodes.LMUL;
	private static final InstructionSearcher.Constraint<AbstractInsnNode> getFieldConstraint = instr -> instr.getOpcode() == Opcodes.GETFIELD || instr.getOpcode() == Opcodes.GETSTATIC;
	private static final InstructionSearcher.Constraint<AbstractInsnNode> putFieldConstraint = instr -> instr.getOpcode() == Opcodes.PUTFIELD || instr.getOpcode() == Opcodes.PUTSTATIC;

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		for (ClassNode classNode : classNodes) {
			for (MethodNode method : classNode.methods) {
				InstructionSearcher searcher = new InstructionSearcher(method);
				FieldInsnNode current;
				while ((current = searcher.next(FieldInsnNode.class, null)) != null) {
					AbstractInsnNode prev = searcher.left();
					LdcInsnNode ldcInsnNode = null;
					if (getFieldConstraint.accept(current)) {
						if (!searcher.hasNext()) {
							continue;
						}
						AbstractInsnNode next = searcher.right();
						if (searcher.hasPrevious() && prev instanceof LdcInsnNode && multiplierConstraint.accept(next)) {
							ldcInsnNode = (LdcInsnNode) prev;
						}
						if (searcher.right().getOpcode() == Opcodes.IMUL && searcher.left().getOpcode() == Opcodes.LDC) {
							ldcInsnNode = (LdcInsnNode) searcher.left();
						}
						if (next instanceof LdcInsnNode && searcher.hasNext(2) && multiplierConstraint.accept(searcher.right(2))) {
							if (searcher.hasNext(3) && putFieldConstraint.accept(searcher.right(3))) {
								continue;
							}
							ldcInsnNode = (LdcInsnNode) next;
						}
					} else {
						if (!searcher.hasPrevious(3) || !multiplierConstraint.accept(prev)) {
							continue;
						}
						if (searcher.left(2) instanceof LdcInsnNode) {
							ldcInsnNode = (LdcInsnNode) searcher.left(2);
						} else if (searcher.left(3) instanceof LdcInsnNode) {
							ldcInsnNode = (LdcInsnNode) searcher.left(3);
						} else if (searcher.hasPrevious(5) && searcher.left(5) instanceof LdcInsnNode && searcher.left(3) instanceof IntInsnNode) {
							ldcInsnNode = (LdcInsnNode) searcher.left(5);
						}
					}

					if (ldcInsnNode == null) {
						continue;
					}
					if (ldcInsnNode.cst instanceof Integer || ldcInsnNode.cst instanceof Long) {
						FieldNode fieldNode = ClassNodeUtils.getField(classNode, current.name, current.desc);
						if (fieldNode == null) {
							continue;
						}
						long value = ldcInsnNode.cst instanceof Long ? (long)ldcInsnNode.cst : (int)ldcInsnNode.cst;
						add(new FieldNodeInfo(classNode, fieldNode.name, fieldNode.desc), current.getOpcode(), BigInteger.valueOf(value));
					}
				}
			}
		}
	}

	private void add(FieldNodeInfo fieldInfo, int opcode, BigInteger value) {
		Map<Integer, List<BigInteger>> values = pairs.get(fieldInfo);
		if (values == null) {
			values = new HashMap<>();
			pairs.put(fieldInfo, values);
		}
		List<BigInteger> opcodeValues = values.get(opcode);
		if (opcodeValues == null) {
			opcodeValues = new ArrayList<>();
			values.put(opcode, opcodeValues);
		}
		if (!opcodeValues.contains(value)) {
			opcodeValues.add(value);
		}
	}

	private static final PairCondition defaultPairCondition = (original, value) -> original == value;

	public static Pair getPair(FieldNodeInfo node) {
		return getPair(node, null);
	}

	public static Optional<Pair> getPair(String className, String fieldName) {
		for (Map.Entry<FieldNodeInfo, Pair> pair : identifiedPairs.entrySet()) {
			FieldNodeInfo fieldNode = pair.getKey();
			if (fieldNode.getOwner().name.equals(className) && fieldNode.getName().equals(fieldName)) {
				return Optional.of(pair.getValue());
			}
		}
		return Optional.empty();
	}

	public static Pair getPair(FieldNodeInfo fieldInfo, PairCondition condition) {
		if (identifiedPairs.containsKey(fieldInfo)) {
			return identifiedPairs.get(fieldInfo);
		}
		FieldNode fieldNode = ClassNodeUtils.getField(fieldInfo.getOwner(), fieldInfo.getName(), fieldInfo.getDescription());
		Map<Integer, List<BigInteger>> values = pairs.get(fieldInfo);
		if (values == null) {
			return null;
		}
		boolean isStatic = (fieldNode.access & Opcodes.ACC_STATIC) != 0;
		List<BigInteger> getValues = values.get(isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD);
		List<BigInteger> putValues = values.get(isStatic ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD);
		if((putValues == null || putValues.isEmpty()) && getValues != null && !getValues.isEmpty()) {
			BigInteger get = getValues.get(0);
			return new Pair(get, get);
		}
		if (getValues == null || putValues == null) {
			return null;
		}
		if (condition == null) {
			condition = defaultPairCondition;
		}
		for (BigInteger put : putValues) {
			for (BigInteger get : getValues) {
				int value = 50;
				int euclideanValue = (value * put.intValue()) * get.intValue();
				if (condition.accept(value, euclideanValue)) {
					Pair pair = new Pair(get, put);
					identifiedPairs.put(fieldInfo, pair);
					return pair;
				}
			}
		}
		return null;
	}

	public static interface PairCondition {
		public boolean accept(int original, int value);
	}

	public static class Pair {

		private final BigInteger get;
		private final BigInteger put;

		public Pair(BigInteger get, BigInteger put) {
			this.get = get;
			this.put = put;
		}

		public BigInteger get() {
			return get;
		}

		public BigInteger put() {
			return put;
		}

		@Override
		public String toString() {
			return "Pair[" + get.intValue() + "," + put.intValue() + "]";
		}

	}

}