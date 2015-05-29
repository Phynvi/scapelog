package com.scapelog.client.loader.analyser;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class OperationAttributes {

	private final Map<String, Object> attributes = Maps.newHashMap();

	public boolean has(String key) {
		return attributes.containsKey(key);
	}

	public void set(String key, Object value) {
		attributes.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
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

}