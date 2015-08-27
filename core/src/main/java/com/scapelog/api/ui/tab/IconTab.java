package com.scapelog.api.ui.tab;

import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.GlyphsDude;

public abstract class IconTab extends BaseTab {

	public IconTab(GlyphIcons icon, String tooltip) {
		super(tooltip);
		GlyphsDude.setIcon(tab, icon, "16.0");
	}

}