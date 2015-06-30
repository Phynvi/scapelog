package com.scapelog.client.loader.analyser.injection;

import org.objectweb.asm.tree.ClassNode;

public abstract class Injection {

	public abstract boolean execute(ClassNode classNode);

}