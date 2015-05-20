package com.scapelog.api.plugin;

import com.scapelog.api.ClientFeature;
import de.jensd.fx.fontawesome.AwesomeIcon;

import java.util.Optional;

public abstract class ButtonPlugin extends Plugin {

	public ButtonPlugin(TabMode tabMode, AwesomeIcon icon, String name) {
		super(tabMode, icon, name);
	}

	public ButtonPlugin(TabMode tabMode, AwesomeIcon icon, String name, Optional<String> configSectionName) {
		super(tabMode, icon, name, configSectionName);
	}

	public ButtonPlugin(TabMode tabMode, AwesomeIcon icon, String name, Optional<String> configSectionName, ClientFeature... dependingFeatures) {
		super(tabMode, icon, name, configSectionName, dependingFeatures);
	}

	@Override
	public OpenTechnique getOpenTechnique() {
		return OpenTechnique.DRAWER;
	}
}