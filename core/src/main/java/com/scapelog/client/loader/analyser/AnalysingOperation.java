package com.scapelog.client.loader.analyser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.scapelog.client.loader.analyser.injection.Injection;

import java.util.List;
import java.util.Map;

public final class AnalysingOperation extends Operation {

	private final Map<String, List<Injection>> classInjections = Maps.newHashMap();

	public AnalysingOperation(OperationAttributes attributes) {
		super(attributes);
	}

	public void addInjection(String nodeName, Injection injection) {
		List<Injection> injections = classInjections.get(nodeName);
		if (injections == null) {
			injections = Lists.newArrayList();
			injections.add(injection);
			classInjections.put(nodeName, injections);
			return;
		}
		injections.add(injection);
	}

	public Map<String, List<Injection>> getClassInjections() {
		return classInjections;
	}

}