package com.scapelog.client.loader.analyser.impl;

import com.scapelog.agent.util.InjectionUtils;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.api.ClientFeature;
import com.scapelog.client.loader.analyser.Analyser;
import com.scapelog.client.loader.analyser.AnalysingOperation;
import com.scapelog.client.loader.analyser.ClassInjection;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;

public final class IdleResetAnalyser extends Analyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation) {
		for(ClassNode node : classNodes) {
			String[] eventNames = {
					"mousePressed", "mouseDragged", "mouseWheelMoved", "mouseMoved", "mouseReleased", "keyPressed", "keyReleased"
			};
			for (MethodNode methodNode : node.methods) {
				String methodName = methodNode.name;
				for (String eventName : eventNames) {
					if (methodName.equals(eventName)) {
						InsnList instructions = InjectionUtils.createPrintInjection(ClientFeature.IDLE_RESET.getIdentifier());
						InjectionUtils.inject(methodNode, null, instructions);
						operation.addInjection(node.name, new ClassInjection(new MethodInfo(node.name, methodNode.name, methodNode.desc), 0, instructions, ClientFeature.IDLE_RESET));
					}
				}
			}
		}
	}

}