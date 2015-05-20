package com.scapelog.api.ui.tab;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;

public abstract class IconTab extends BaseTab {

	public IconTab(AwesomeIcon icon, String tooltip) {
		super(tooltip);
		AwesomeDude.setIcon(tab, icon);
	}

}