package com.scapelog.client.loader.analyser;

import com.google.common.collect.ImmutableList;
import com.scapelog.agent.util.tree.MethodInfo;
import com.scapelog.api.ClientFeature;
import org.objectweb.asm.tree.InsnList;

public final class ClassInjection {

	private final MethodInfo methodInfo;

	private final int index;

	private final InsnList instructions;

	private final ImmutableList<ClientFeature> features;

	public ClassInjection(MethodInfo methodInfo, int index, InsnList instructions, ClientFeature... features) {
		this.methodInfo = methodInfo;
		this.index = index;
		this.instructions = instructions;
		this.features = features == null ? ImmutableList.of() : ImmutableList.copyOf(features);
	}

	public MethodInfo getMethodInfo() {
		return methodInfo;
	}

	public int getIndex() {
		return index;
	}

	public InsnList getInstructions() {
		return instructions;
	}

	public ImmutableList<ClientFeature> getFeatures() {
		return features;
	}

}