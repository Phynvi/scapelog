package com.scapelog.client.loader.analyser.impl.reflection;

import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.agent.util.InsnNodeUtils;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.impl.ReflectionAnalyser;
import com.scapelog.client.reflection.ReflectedField;
import com.scapelog.client.reflection.ReflectedFields;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlayerAnalyser extends ReflectionAnalyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		ClassNode clientNode = ClassNodeUtils.getClassNode(classNodes, "client");
		MethodNode clinit = ClassNodeUtils.getMethod(clientNode, "<clinit>");
		if (clinit == null) {
			return;
		}

		String className = null;

		InstructionSearcher searcher = new InstructionSearcher(clinit);
		List<AbstractInsnNode[]> patterns = searcher.findPatterns("SIPUSH ANEWARRAY PUTSTATIC");
		for (AbstractInsnNode[] pattern : patterns) {
			IntInsnNode sipush = (IntInsnNode) pattern[0];
			if (sipush.operand != 2048) {
				continue;
			}
			FieldInsnNode field = (FieldInsnNode) pattern[2];
			if (field == null) {
				continue;
			}
			if (!InsnNodeUtils.isPublic(classNodes, field)) {
				continue;
			}
			ReflectedFields.LOCAL_PLAYERS.setClassName(field.owner).setFieldName(field.name);
			className = findClassName(field.desc, "[\\[|L]*(.*)[;]*");
		}
		if (className == null) {
			return;
		}
		ClassNode playerClass = ClassNodeUtils.getClassNode(classNodes, className);
		if (playerClass == null) {
			return;
		}
		findUsername(playerClass);
	}

	private void findUsername(ClassNode classNode) {
		for (MethodNode methodNode : classNode.methods) {
			InstructionSearcher searcher = new InstructionSearcher(methodNode);
			if(searcher.next(IntInsnNode.class, instr -> instr.getOpcode() == Opcodes.BIPUSH && instr.operand == 64 && instr.getNext().getOpcode() == Opcodes.IAND) == null) {
				continue;
			}
			searcher.resetIndex();

			if (searcher.next(Opcodes.IF_ACMPNE) == null) {
				continue;
			}
			FieldInsnNode usernameField = (FieldInsnNode) searcher.next(Opcodes.GETFIELD);
			if (usernameField == null) {
				continue;
			}
			ReflectedFields.PLAYER_USERNAME.setClassName(classNode.name).setFieldName(usernameField.name);
			break;
		}
	}

	private static String findClassName(String str, String regex) {
		try {
			final Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				return matcher.group(1).replace(";", "");
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	@Override
	public ReflectedField[] getRequiredFields() {
		return new ReflectedField[] {
				ReflectedFields.LOCAL_PLAYERS,
				ReflectedFields.PLAYER_USERNAME
		};
	}

}