package com.scapelog.client.loader.analyser.impl.reflection;

import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.FieldNodeInfo;
import com.scapelog.client.loader.analyser.ReflectionAnalyser;
import com.scapelog.client.loader.analyser.ReflectionOperation;
import com.scapelog.client.reflection.ReflectedField;
import com.scapelog.client.util.Debug;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.List;

public final class WorldTypeAnalyser extends ReflectionAnalyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, ReflectionOperation operation) {
		for (ClassNode classNode : classNodes) {
			MethodNode clinit = ClassNodeUtils.getMethod(classNode, "<clinit>");
			if (clinit == null) {
				continue;
			}
			InstructionSearcher searcher = new InstructionSearcher(clinit);
			searcher.resetIndex();
			List<AbstractInsnNode[]> patterns = searcher.findPatterns("NEW DUP * LDC INVOKESPECIAL PUTSTATIC");
			if (patterns.size() == 0) {
				continue;
			}
			for (AbstractInsnNode[] pattern : patterns) {
				AbstractInsnNode constant = pattern[2];
				FieldInsnNode field = (FieldInsnNode) pattern[5];

				FieldNodeInfo fieldNodeInfo = new FieldNodeInfo(classNode, field.name, field.desc);

				if (constant.getOpcode() == Opcodes.ICONST_0) {
					Debug.println("WorldType.FREE = %s.%s", classNode.name, field.name);
					operation.getAttributes().set("world_type_free", fieldNodeInfo);
				} else if (constant.getOpcode() == Opcodes.ICONST_1) {
					Debug.println("WorldType.MEMBERS = %s.%s", classNode.name, field.name);
					operation.getAttributes().set("world_type_members", fieldNodeInfo);
				}
			}
		}
	}

	@Override
	public ReflectedField[] getRequiredFields() {
		return new ReflectedField[0];
	}

}