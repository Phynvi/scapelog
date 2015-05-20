package com.scapelog.client.loader.analyser;

import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;

public abstract class Analyser {

	public abstract void analyse(Collection<ClassNode> classNodes, AnalysingOperation operation);

}