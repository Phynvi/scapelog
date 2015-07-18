package com.scapelog.client.loader.analyser.impl.reflection;

import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.MethodNodeInfo;
import com.scapelog.client.loader.analyser.ReflectionAnalyser;
import com.scapelog.client.loader.analyser.ReflectionOperation;
import com.scapelog.client.loader.analyser.impl.StringFieldAnalyser;
import com.scapelog.client.reflection.ReflectedField;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.Optional;

public final class MenuBuilderAnalyser extends ReflectionAnalyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, ReflectionOperation operation) {
		Optional<StringFieldAnalyser.TranslatedString> levelString = StringFieldAnalyser.getString("level: ");
		if (!levelString.isPresent()) {
			return;
		}
		StringFieldAnalyser.TranslatedString translatedString = levelString.get();
		FieldInsnNode levelReference = translatedString.getFieldInsnNode();

		for (ClassNode classNode : classNodes) {
			for (MethodNode methodNode : classNode.methods) {
				InstructionSearcher searcher = new InstructionSearcher(methodNode);

				IntInsnNode var = searcher.next(IntInsnNode.class, instr -> instr.getOpcode() == Opcodes.SIPUSH && instr.operand == 2000);
				if (var == null) {
					continue;
				}

				searcher.resetIndex();

				FieldInsnNode fieldNode = searcher.next(FieldInsnNode.class, instr ->
						instr.getOpcode() == Opcodes.GETSTATIC
								&& instr.owner.equals(levelReference.owner)
								&& instr.name.equals(levelReference.name));
				if (fieldNode == null) {
					continue;
				}

				searcher.resetIndex();

				MethodNodeInfo methodNodeInfo = new MethodNodeInfo(classNode, methodNode.name, methodNode.desc);
				if (searcher.next(IntInsnNode.class, instr -> instr.getOpcode() == Opcodes.BIPUSH && instr.operand == 13) != null) {
					operation.getAttributes().set("menu_builder_npc_" + methodNode.name, methodNodeInfo);
				} else {
					operation.getAttributes().set("menu_builder_player_" + methodNode.name, methodNodeInfo);
				}
			}
		}
	}

	@Override
	public ReflectedField[] getRequiredFields() {
		return new ReflectedField[0];
	}

}