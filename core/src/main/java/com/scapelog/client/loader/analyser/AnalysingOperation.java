package com.scapelog.client.loader.analyser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class AnalysingOperation {

	private final Map<String, Object> attributes = Maps.newHashMap();
	private final Map<String, List<ClassInjection>> classInjections = Maps.newHashMap();

	public boolean hasAttribute(String key) {
		return attributes.containsKey(key);
	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		if (key.startsWith("*")) {
			key = key.substring(1);
			List<Object> attributes = new ArrayList<>();
			for (Map.Entry<String, Object> entry : this.attributes.entrySet()) {
				if (entry.getKey().endsWith(key)) {
					attributes.add(entry.getValue());
				}
			}
			return (T) attributes;
		} else if (key.endsWith("*")) {
			key = key.substring(0, key.length() - 1);
			List<Object> attributes = new ArrayList<>();
			for (Map.Entry<String, Object> entry : this.attributes.entrySet()) {
				if (entry.getKey().startsWith(key)) {
					attributes.add(entry.getValue());
				}
			}
			return (T) attributes;
		} else {
			return (T) this.attributes.get(key);
		}
	}

	public void addInjection(String nodeName, ClassInjection injection) {
		List<ClassInjection> injections = classInjections.get(nodeName);
		if (injections == null) {
			injections = Lists.newArrayList();
			classInjections.put(nodeName, injections);
		}
		injections.add(injection);
	}

	public Map<String, List<ClassInjection>> getClassInjections() {
		return classInjections;
	}

}