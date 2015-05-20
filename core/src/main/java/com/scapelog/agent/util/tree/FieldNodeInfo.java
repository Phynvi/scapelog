package com.scapelog.agent.util.tree;

import org.objectweb.asm.tree.ClassNode;

public final class FieldNodeInfo extends NodeInfo<ClassNode> {

	public FieldNodeInfo(ClassNode owner, String fieldName, String description) {
		super(owner, fieldName, description);
	}

}