package com.scapelog.client.loader.analyser.impl;

import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.agent.util.InstructionSearcher;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.injection.ReplaceInjection;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;

public final class CameraZoomAnalyser extends Analyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		FieldInsnNode maxZoomField = null;
		FieldInsnNode minZoomField = null;
		for (ClassNode classNode : classNodes) {
			for (MethodNode methodNode : classNode.methods) {
				try {
					InstructionSearcher searcher = new InstructionSearcher(methodNode);
					boolean valid = false;
					while (searcher.next(IntInsnNode.class, instr -> instr.operand == 334) != null) {
						if (searcher.current().getOpcode() != Opcodes.ISUB && searcher.right(2).getOpcode() != Opcodes.ISTORE) {
							continue;
						}
						valid = true;
						break;
					}
					if (!valid) {
						continue;
					}
					valid = false;
					searcher.resetIndex();
					while (searcher.next(IntInsnNode.class, instr -> instr.operand == 14) != null) {
						if (searcher.right().getOpcode() != Opcodes.ISHR) {
							continue;
						}
						valid = true;
						break;
					}
					if (!valid) {
						continue;
					}
					System.out.println("camera_zoom: " + classNode.name + "." + methodNode.name);
					searcher.resetIndex();
					maxZoomField = (FieldInsnNode) searcher.next(Opcodes.GETSTATIC);
					minZoomField = (FieldInsnNode) searcher.next(Opcodes.GETSTATIC);
					System.out.println("\tmax_zoom: " + maxZoomField.owner + "." + maxZoomField.name + ":" + maxZoomField.desc);
					System.out.println("\tmin_zoom: " + minZoomField.owner + "." + minZoomField.name + ":" + minZoomField.desc);
				} catch (Exception e) {
					/**/
				}
			}
		}
		if (maxZoomField != null) {
			editValue(classNodes, operation, maxZoomField, 500);
		}
		if (minZoomField != null) {
			editValue(classNodes, operation, minZoomField, 10);
		}
	}

	private void editValue(Collection<ClassNode> classNodes, AnalysingOperation operation, FieldInsnNode fieldInsnNode, int newValue) {
		ClassNode classNode = ClassNodeUtils.getClassNode(classNodes, fieldInsnNode.owner);
		if (classNode == null) {
			return;
		}
		MethodNode clinit = ClassNodeUtils.getMethod(classNode, "<clinit>");
		InstructionSearcher searcher = new InstructionSearcher(clinit);
		while (searcher.next(Opcodes.PUTSTATIC) != null) {
			FieldInsnNode putStatic = (FieldInsnNode) searcher.current();
			if (ClassNodeUtils.fieldInsnNodeEquals(fieldInsnNode, putStatic)) {
				int index = searcher.getIndex();
				IntInsnNode sipush = (IntInsnNode) searcher.previous(Opcodes.SIPUSH);
				searcher.setIndex(index);

				System.out.println("\t\t" + sipush.operand);
				IntInsnNode newSipush = new IntInsnNode(Opcodes.SIPUSH, newValue);

				MethodInfo methodInfo = new MethodInfo(classNode.name, clinit.name, clinit.desc);
				index = clinit.instructions.indexOf(sipush);
				operation.addInjection(classNode.name, new ReplaceInjection(methodInfo, index, newSipush));
				clinit.instructions.set(sipush, newSipush);
			}
		}
	}

}