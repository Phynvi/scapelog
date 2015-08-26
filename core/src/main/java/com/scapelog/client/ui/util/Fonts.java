package com.scapelog.client.ui.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.CharSink;
import com.google.common.io.Resources;
import javafx.scene.Scene;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Fonts {

	private static final Pattern TTF_PATTERN = Pattern.compile("http:.*\\.ttf");

	private static final Pattern OTF_PATTERN = Pattern.compile("http:.*\\.otf");

	private static final String FONT_URL = "http://fonts.googleapis.com/css?family=";

	public static void addDefaults(Scene node) {
		addDefault(node, "Open Sans");
	}

	private static void addDefault(Scene node, String name) {
		node.getStylesheets().add(Fonts.getWebFontStyleSheet(name));
	}

	public static String getWebFontStyleSheet(String name) {
		name = name.replace(" ", "+").replace(".css", "");
		Path stylesheet = Fonts.getWebFontFile(name);
		if (stylesheet == null) {
			return FONT_URL + name;
		}
		return "file://" + stylesheet.toString();
	}

	private static Path getWebFontFile(String name) {
		try {
			String stylesheetPath = System.getProperty("user.home") + "/.scapelog/fonts/";
			String stylesheetName = name + ".css";
			stylesheetName = stylesheetName.replace("+", "");
			Path stylesheet = Paths.get(stylesheetPath, stylesheetName);

			if (Files.exists(stylesheet)) {
				return stylesheet;
			}

			List<String> newLines = Lists.newArrayList();
			List<String> lines = Resources.readLines(new URL(FONT_URL + name), Charsets.UTF_8);
			for (String line : lines) {
				Matcher matcher = TTF_PATTERN.matcher(line);
				if (matcher.find()) {
					String fontUrl = matcher.group(0);
					String fileName = new URL(fontUrl).getFile();

					Path filePath = Paths.get(stylesheetPath, fileName);
					if (Files.notExists(filePath)) {
						Files.createDirectories(filePath.getParent());
						try (InputStream in = new URL(fontUrl).openStream()) {
							Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
						}
					}
					line = line.replace(fontUrl, filePath.toString());
				}
				newLines.add(line);
			}

			CharSink sink = com.google.common.io.Files.asCharSink(new File(stylesheet.toString()), Charsets.UTF_8);
			sink.writeLines(newLines);

			return stylesheet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
