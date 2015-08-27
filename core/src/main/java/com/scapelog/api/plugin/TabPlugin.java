package com.scapelog.api.plugin;

import com.scapelog.api.ClientFeature;
import de.jensd.fx.glyphs.GlyphIcons;

import java.util.Optional;

public abstract class TabPlugin extends Plugin {

	public TabPlugin(GlyphIcons icon, String name) {
		super(TabMode.ON, icon, name);
	}

	public TabPlugin(GlyphIcons icon, String name, Optional<String> configSectionName) {
		super(TabMode.ON, icon, name, configSectionName);
	}

	public TabPlugin(GlyphIcons icon, String name, Optional<String> configSectionName, ClientFeature... dependingFeatures) {
		super(TabMode.ON, icon, name, configSectionName, dependingFeatures);
	}

}