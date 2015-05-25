package com.scapelog.client.loader;

import com.goebl.david.WebbException;
import com.google.common.collect.Lists;
import com.scapelog.client.model.Language;
import com.scapelog.client.model.WorldList;
import com.scapelog.client.ui.util.WebUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class JavConfig {
	public static final String GAMEPACK_NAME = "initial_jar";
	public static final String MAIN_CLASS = "initial_class";
	public static final String CODE_BASE = "codebase";

	public static final String SECRET_PARAMETER_NAME = "0";
	public static final String VECTOR_PARAMETER_NAME = "-1";

	private final ClientLoader clientLoader;
	private final Optional<WorldList> world;
	private final Language language;

	private final Map<String, String> config = new HashMap<>();
	private final Map<String, String> params = new HashMap<>();

	public JavConfig(ClientLoader clientLoader, Optional<WorldList> world, Language language) {
		this.clientLoader = clientLoader;
		this.world = world;
		this.language = language;
	}

	public boolean load() {
		try {
			List<String> lines = getConfigPage(getURL(world));
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
			return !(params.isEmpty() || config.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/* This will keep trying to find a working world, instead of trying just once like you had previous. */
	private List<String> getConfigPage(String baseUrl) {
		try {
			return WebUtils.readLines(baseUrl, "/jav_config.ws");
		} catch (WebbException e) {
			WorldList randomWorld = WorldList.getRandomWorld();
			clientLoader.print("- Connection timed out, attempting world " + randomWorld.getId());
			return getConfigPage(getURL(Optional.of(randomWorld)));
		}
	}

	private String getURL(Optional<WorldList> world) {
		return "http://" + (world.isPresent() ? "world" + world.get().getId() + "." : "") + "runescape.com/l=" + language.getLanguage();
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