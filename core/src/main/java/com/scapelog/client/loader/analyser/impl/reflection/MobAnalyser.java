package com.scapelog.client.loader.analyser.impl.reflection;

import com.scapelog.agent.util.ClassNodeUtils;
import com.scapelog.client.loader.analyser.ReflectionAnalyser;
import com.scapelog.client.loader.analyser.ReflectionOperation;
import com.scapelog.client.reflection.ClassNames;
import com.scapelog.client.reflection.ReflectedField;
import com.scapelog.client.util.Debug;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;

public final class MobAnalyser extends ReflectionAnalyser {

	@Override
	public void analyse(Collection<ClassNode> classNodes, ReflectionOperation operation) {
		String playerName = ClassNames.PLAYER;
		if (playerName == null) {
			return;
		}
		ClassNode playerClassNode = ClassNodeUtils.getClassNode(classNodes, playerName);
		if (playerClassNode == null) {
			return;
		}
		ClassNames.MOB = playerClassNode.superName;
		Debug.println("mob identified as %s", ClassNames.MOB);
	}

	@Override
	public ReflectedField[] getRequiredFields() {
		return new ReflectedField[0];
	}

}