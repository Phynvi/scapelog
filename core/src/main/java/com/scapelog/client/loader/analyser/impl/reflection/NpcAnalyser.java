package com.scapelog.client.loader.analyser.impl.reflection;

import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.MethodNodeInfo;
import com.scapelog.client.loader.analyser.ReflectionAnalyser;
import com.scapelog.client.loader.analyser.ReflectionOperation;
import com.scapelog.client.loader.analyser.impl.StringFieldAnalyser;
import com.scapelog.client.reflection.ClassNames;
import com.scapelog.client.reflection.ReflectedField;
import com.scapelog.client.reflection.ReflectedFields;
import com.scapelog.client.util.Debug;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class NpcAnalyser extends ReflectionAnalyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, ReflectionOperation operation) {
		String mobName = ClassNames.MOB;
		if (mobName == null) {
			return;
		}
		for (ClassNode classNode : classNodes) {
			if (classNode.name.equals(ClassNames.PLAYER)) {
				continue;
			}
			if (classNode.superName.equals(mobName) && !classNode.superName.equals(classNode.name)) {
				ClassNames.NPC = classNode.name;
				Debug.println("npc identified as %s", ClassNames.NPC);
				break;
			}
		}
		findFields(operation);
	}

	private void findFields(ReflectionOperation operation) {
		List<MethodNodeInfo> menuBuilders = operation.getAttributes().get("menu_builder_npc_*");
		if (menuBuilders.isEmpty()) {
			return;
		}

		Optional<StringFieldAnalyser.TranslatedString> levelString = StringFieldAnalyser.getString("level: ");
		if (!levelString.isPresent()) {
			return;
		}
		StringFieldAnalyser.TranslatedString translatedString = levelString.get();
		FieldInsnNode levelReference = translatedString.getFieldInsnNode();

		for (MethodNodeInfo methodNodeInfo : menuBuilders) {
			ClassNode classNode = methodNodeInfo.getOwner();
			MethodNode methodNode = ClassNodeUtils.getMethod(classNode, methodNodeInfo.getName(), methodNodeInfo.getDescription());
			InstructionSearcher searcher = new InstructionSearcher(methodNode);

			if (!ReflectedFields.NPC_NAME.getFieldName().isPresent()) {
				FieldInsnNode nameFieldNode = searcher.next(FieldInsnNode.class, instr -> instr.desc.equals("Ljava/lang/String;"));
				ReflectedFields.NPC_NAME.setClassName(nameFieldNode.owner).setFieldName(nameFieldNode.name);
				Debug.println("npc_name identified as %s.%s", nameFieldNode.owner, nameFieldNode.name);
			}

			searcher.resetIndex();

			while(searcher.next(Opcodes.GETSTATIC) != null) {
				FieldInsnNode field = searcher.current();
				if (!field.owner.equals(levelReference.owner) || !field.name.equals(levelReference.name)) {
					continue;
				}
				FieldInsnNode combatCall = (FieldInsnNode) searcher.next(Opcodes.GETFIELD);
				if (combatCall == null) {
					continue;
				}
				boolean players = false;

				if (ClassNames.PLAYER != null && ClassNames.PLAYER.equals(combatCall.owner)) {
					if (!ReflectedFields.PLAYER_COMBAT_LEVEL.getClassName().isPresent()) {
						ReflectedFields.PLAYER_COMBAT_LEVEL.setClassName(combatCall.owner).setFieldName(combatCall.name);
						Debug.println("player_combat identified as %s.%s:%s", combatCall.owner, combatCall.name, combatCall.desc);
						players = true;
					}
				}

				if (!players) {
					if (!ReflectedFields.NPC_COMBAT_LEVEL.getClassName().isPresent()) {
						ReflectedFields.NPC_COMBAT_LEVEL.setClassName(combatCall.owner).setFieldName(combatCall.name);
						Debug.println("npc_combat identified as %s.%s:%s", combatCall.owner, combatCall.name, combatCall.desc);
					}
				}
			}
		}
	}

	@Override
	public ReflectedField[] getRequiredFields() {
		return new ReflectedField[] {
				ReflectedFields.PLAYER_COMBAT_LEVEL,
				ReflectedFields.NPC_COMBAT_LEVEL,
				ReflectedFields.NPC_NAME
		};
	}

}