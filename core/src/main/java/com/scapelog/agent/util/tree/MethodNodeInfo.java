package com.scapelog.agent.util.tree;

import org.objectweb.asm.tree.ClassNode;

public final class MethodNodeInfo extends NodeInfo<ClassNode> {

	public MethodNodeInfo(ClassNode owner, String fieldName, String description) {
		super(owner, fieldName, description);
	}

}