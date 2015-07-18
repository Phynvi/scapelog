package com.scapelog.api.ui;

import com.google.common.collect.Maps;

import java.awt.Font;
import java.util.Map;

public enum RSFont {
	DIALOGUE("rs_dialogue_font_16", 16),
	FANCY_LARGE("rs_fancy_font_large_32", 32),
	FANCY_MEDIUM("rs_fancy_font_medium_32", 32),
	FANCY_SMALL("rs_fancy_font_small_32", 32),
	GRAVESTONE("rs_gravestone_font_16", 16),
	SMALL("rs_login_font_16", 16),
	MEDIUM("rs_small_font_16", 16),
	MEDIUM_BOLD("rs_small_font_bold_16", 16),
	TINY("rs_tiny_font_16", 16);

	private final String fileName;
	private final float defaultSize;

	private Font font;

	private final Map<Float, Font> fontCache = Maps.newHashMap();

	RSFont(String fileName, float defaultSize) {
		this.fileName = fileName;
		this.defaultSize = defaultSize;
	}

	public Font getFont() {
		return getFont(defaultSize);
	}

	public Font getFont(float size) {
		Font font = fontCache.get(size);
		if (font == null) {
			font = createFont().deriveFont(size);
			fontCache.put(size, font);
		}
		return font;
	}

	private Font createFont() {
		if (font == null) {
			try {
				font = Font.createFont(Font.TRUETYPE_FONT, RSFont.class.getResourceAsStream("/fonts/" + fileName + ".ttf"));
			} catch (Exception e) {
				return null;
			}
		}
		return font;
	}

}