package com.scapelog.client.loader.analyser;

import com.scapelog.client.reflection.ReflectedField;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;

public abstract class ReflectionAnalyser {

	public abstract void analyse(Collection<ClassNode> classNodes, ReflectionOperation operation);

	public abstract ReflectedField[] getRequiredFields();

}