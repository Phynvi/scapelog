package com.scapelog.client.loader.analyser.impl;

import com.scapelog.agent.util.InjectionUtils;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.api.ClientFeature;
import com.scapelog.client.jagex.jaggl.MouseDetour;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.injection.ClassInjection;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.awt.event.MouseEvent;
import java.util.Collection;

public final class InputEventAnalyser extends Analyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		for(ClassNode classNode : classNodes) {
			String[] eventNames = {
					"mousePressed", "mouseDragged", "mouseWheelMoved", "mouseMoved", "mouseReleased", "keyPressed", "keyReleased"
			};
			for (MethodNode methodNode : classNode.methods) {
				String methodName = methodNode.name;
				for (String eventName : eventNames) {
					if (!methodName.equals(eventName)) {
						continue;
					}

					if (methodName.equals("mousePressed") || methodName.equals("mouseDragged") || methodName.equals("mouseMoved") || methodName.equals("mouseReleased")) {
						modifyMouseEvent(classNode, methodNode, operation);
					}

					InsnList instructions = InjectionUtils.createEventInjection(ClientFeature.IDLE_RESET.getIdentifier());
					InjectionUtils.inject(methodNode, null, instructions);
					operation.addInjection(classNode.name, new ClassInjection(new MethodInfo(classNode.name, methodNode.name, methodNode.desc), 0, instructions, ClientFeature.IDLE_RESET));

				}
			}
		}
	}

	private void modifyMouseEvent(ClassNode classNode, MethodNode methodNode, AnalysingOperation operation) {
		LabelNode label = new LabelNode();

		InsnList instructions = new InsnList();
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(MouseDetour.class), methodNode.name, Type.getMethodDescriptor(Type.getType(MouseEvent.class), Type.getType(MouseEvent.class)), false));
		instructions.add(new VarInsnNode(Opcodes.ASTORE, 1));
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(MouseEvent.class), "isConsumed", Type.getMethodDescriptor(Type.BOOLEAN_TYPE), false));
		instructions.add(new JumpInsnNode(Opcodes.IFEQ, label));
		instructions.add(new InsnNode(Opcodes.RETURN));
		instructions.add(label);

		InjectionUtils.inject(methodNode, null, instructions);
		MethodInfo methodInfo = new MethodInfo(classNode.name, methodNode.name, methodNode.desc);
		operation.addInjection(classNode.name, new ClassInjection(methodInfo, 0, instructions));
	}

}