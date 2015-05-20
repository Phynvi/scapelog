package com.scapelog.api.plugin;

import com.scapelog.api.ClientFeature;
import de.jensd.fx.fontawesome.AwesomeIcon;

import java.util.Optional;

public abstract class TabPlugin extends Plugin {

	public TabPlugin(AwesomeIcon icon, String name) {
		super(TabMode.ON, icon, name);
	}

	public TabPlugin(AwesomeIcon icon, String name, Optional<String> configSectionName) {
		super(TabMode.ON, icon, name, configSectionName);
	}

	public TabPlugin(AwesomeIcon icon, String name, Optional<String> configSectionName, ClientFeature... dependingFeatures) {
		super(TabMode.ON, icon, name, configSectionName, dependingFeatures);
	}

}