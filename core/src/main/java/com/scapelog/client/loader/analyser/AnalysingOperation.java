package com.scapelog.client.loader.analyser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public final class AnalysingOperation extends Operation {

	private final Map<String, List<ClassInjection>> classInjections = Maps.newHashMap();

	public AnalysingOperation(OperationAttributes attributes) {
		super(attributes);
	}

	public void addInjection(String nodeName, ClassInjection injection) {
		List<ClassInjection> injections = classInjections.get(nodeName);
		if (injections == null) {
			injections = Lists.newArrayList();
			injections.add(injection);
			classInjections.put(nodeName, injections);
			return;
		}
		injections.add(injection);
	}

	public Map<String, List<ClassInjection>> getClassInjections() {
		return classInjections;
	}

}