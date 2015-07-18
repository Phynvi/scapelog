package com.scapelog.agent.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class InstructionSearcher {

	public interface Constraint<T> {
		boolean accept(T instr);
	}

	private final AbstractInsnNode[] instructions;
    private int index;

    private static final Map<String, List<Integer>> patternCache = new LinkedHashMap<>();
    private static final Map<String, Integer> opcodeCache = new LinkedHashMap<>();

    public InstructionSearcher(MethodNode method) {
        this.instructions = method.instructions.toArray();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

	public void resetIndex() {
		setIndex(-1);
	}

    public <T extends AbstractInsnNode> T current() {
        return (index < 0 || index >= instructions.length) ? null : (T) instructions[index];
    }

    public AbstractInsnNode next(int opcode) {
        while (++index < instructions.length) {
            AbstractInsnNode instr = instructions[index];
            if (instr.getOpcode() == opcode) {
                return instr;
            }
        }
        return null;
    }

    public AbstractInsnNode next() {
	    ++index;
        return current();
    }

    public AbstractInsnNode previous() {
	    --index;
        return current();
    }

	public AbstractInsnNode previous(int opcode) {
		try {
			while (--index < instructions.length) {
				AbstractInsnNode instr = instructions[index];
				if(instr.getOpcode() == opcode) {
					return instr;
				}
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			/**/
		}
		return null;
	}

	public boolean hasNext() {
		return hasNext(1);
	}

	public boolean hasNext(int n) {
		boolean has = true;
		for(int i = 0; i < n; i++)
			if(next() == null)
				has = false;
		index -= n;
		return has;
	}

	public boolean hasPrevious() {
		return hasPrevious(1);
	}

	public boolean hasPrevious(int n) {
		boolean has = true;
		for(int i = 0; i < n; i++)
			if(previous() == null)
				has = false;
		index += n;
		return has;
	}

	public AbstractInsnNode left(int n) {
        int tempIndex = index - n;
        return (tempIndex < 0 || tempIndex > instructions.length) ? null : instructions[tempIndex];
    }

    public AbstractInsnNode right(int n) {
        int tempIndex = index + n;
        return (tempIndex < 0 || tempIndex > instructions.length) ? null : instructions[tempIndex];
    }

    public AbstractInsnNode left() {
        return left(1);
    }

    public AbstractInsnNode right() {
        return right(1);
    }

    @SuppressWarnings("unchecked")
	public <T> T next(Class<T> type, Constraint<T> constr) {
		try {
			while (++index < instructions.length) {
				AbstractInsnNode instr = instructions[index];
				if (type.isAssignableFrom(instr.getClass()) && (constr == null || constr.accept((T) instr))) {
					return type.cast(instr);
				}
			}
		} catch(ArrayIndexOutOfBoundsException e) {}
		return null;
	}

	public <T> T previous(Class<T> type, int opcode) {
		try {
			while (--index >= 0) {
				AbstractInsnNode instr = instructions[index];
				if (type.isAssignableFrom(instr.getClass()) && instr.getOpcode() == opcode) {
					return type.cast(instr);
				}
			}
		} catch(ArrayIndexOutOfBoundsException e) {}
		return null;
	}

	public LdcInsnNode nextLDC(final Object val) {
		Constraint<LdcInsnNode> constraint = ldc -> val.getClass().equals(ldc.cst.getClass()) && ldc.cst.equals(val);
		return next(LdcInsnNode.class, constraint);
	}

	public int indexOf(AbstractInsnNode node) {
		for(int idx = 0; idx < instructions.length; idx++) {
			if(instructions[idx].equals(node)) {
				return idx;
			}
		}
		return -1;
	}

	public List<AbstractInsnNode> getAllByOpcode(int opcode) {
		List<AbstractInsnNode> nodes = new ArrayList<>();
		for(AbstractInsnNode node : instructions) {
			if(node.getOpcode() == opcode)
				nodes.add(node);
		}
		return nodes;
	}

    public List<AbstractInsnNode[]> findPatterns(final String pattern) {
        List<AbstractInsnNode[]> matches = new LinkedList<>();
        String[] parts = pattern.split(" ");
        int nodeIdx = 0;
        int patternIdx = 0;
        outer: for (int idx = ++index; idx < instructions.length; ++idx) {
            AbstractInsnNode ain = instructions[idx];
            String part = parts[patternIdx];
            List<Integer> codes = compilePattern(part);
            for (int code : codes) {
                if (part.startsWith("!")) {
                    if (code == ain.getOpcode()) {
                        continue;
                    }
                } else if (code != ain.getOpcode() && !part.equals("*")) {
                    continue;
                }
                if (patternIdx < parts.length - 1) {
                    patternIdx++;
                } else {
                    AbstractInsnNode[] nodes = new AbstractInsnNode[parts.length];
                    System.arraycopy(instructions, nodeIdx, nodes, 0, parts.length);
                    matches.add(nodes);
                    idx = nodeIdx++;
                    index = nodeIdx;
                    patternIdx = 0;
                }
                continue outer;
            }
            idx = nodeIdx++;
            patternIdx = 0;
        }
        return matches;
    }

    private static List<Integer> compilePattern(final String pattern) {
        if (patternCache.containsKey(pattern)) {
            return patternCache.get(pattern);
        }
        final List<Integer> opcodes = new LinkedList<>();
        if (pattern.startsWith("(") && pattern.contains("|") && pattern.endsWith(")")) {
            final String trimmed = pattern.substring(1, pattern.length() - 1);
            final String[] parts = trimmed.split("\\|");
            for (final String insn : parts) {
                opcodes.add(getOpcode(insn.replaceAll("!", "")));
            }
        } else {
            opcodes.add(getOpcode(pattern.replaceAll("!", "")));
        }
        patternCache.put(pattern, opcodes);
        return opcodes;
    }

    private static int getOpcode(final String name) {
        if (opcodeCache.containsKey(name)) {
            return opcodeCache.get(name);
        }
        try {
            Integer opcode = (Integer) Opcodes.class.getField(name.toUpperCase()).get(null);
            opcodeCache.put(name, opcode);
            return opcode;
        } catch (IllegalAccessException e) {
            return -1;
        } catch (NoSuchFieldException e) {
            return -1;
        }
    }

	public int jump(JumpInsnNode jumpInsnNode) {
		resetIndex();
		while (++index < instructions.length) {
			AbstractInsnNode instr = instructions[index];
			if (instr instanceof LabelNode) {
				try {
					LabelNode labelNode = (LabelNode) instr;
					if (labelNode.getLabel().info.equals(jumpInsnNode.label.getLabel().info)) {
						return index;
					}
				} catch (NullPointerException e) {
					/**/
				}
			}
		}
		return index;
	}

	public void jump(LabelNode label) {
		resetIndex();
		while(next(LabelNode.class, null) != null) {
			LabelNode lbl = (LabelNode) current();
			if(lbl.equals(label)) {
				break;
			}
		}
	}

	public void jumpNext() {
		jump((JumpInsnNode) next(Opcodes.GOTO));
	}

	public void gotoJump(LabelNode label) {
		resetIndex();
		while(next(JumpInsnNode.class, null) != null) {
			JumpInsnNode jump = (JumpInsnNode) current();
			if(jump.label.equals(label)) {
				break;
			}
		}
	}

}