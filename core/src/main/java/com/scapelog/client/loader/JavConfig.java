package com.scapelog.client.loader;

import com.google.common.io.Resources;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JavConfig {
	public static final String GAMEPACK_NAME = "initial_jar";
	public static final String MAIN_CLASS = "initial_class";
	public static final String CODE_BASE = "codebase";

	public static final String SECRET_PARAMETER_NAME = "0";
	public static final String VECTOR_PARAMETER_NAME = "-1";

	private final String configUrl;

	private final Map<String, String> config = new HashMap<>();
	private final Map<String, String> params = new HashMap<>();

	public JavConfig(String configUrl) {
		this.configUrl = configUrl;
	}

	public void load() {
		try {
			URL url = new URL(configUrl);
			List<String> lines = Resources.readLines(url, Charset.forName("UTF-8"));
			lines.size();
			for (String line : lines) {
				if(line.length() < 1 || !line.contains("="))
					continue;
				String key = line.substring(0, line.indexOf("="));
				String value = line.substring(key.length() + 1);

				if(key.equals("param")) {
					key = value.substring(0, value.indexOf("="));
					value = value.substring(key.length() + 1);
					params.put(key, value);
				} else if(!key.equals("msg")) {
					config.put(key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getConfig(String name) {
		return config.get(name);
	}

	public String getParameter(String name) {
		return params.get(name);
	}

	public Map<String, String> getParameters() {
		return params;
	}

}