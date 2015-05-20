package com.scapelog.client.ui.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

// todo: move to api?
public final class WebUtils {

	public static Path download(String url, String inputFile, String outputPath, String outputFile) throws Exception {
		return download(url + inputFile, outputPath, outputFile);
	}

	public static Path download(String url, String outputPath, String outputFile) throws Exception {
		try {
			Path output = Paths.get(outputPath);
			if (Files.notExists(output)) {
				Files.createDirectory(output);
			}
			URI u = URI.create(url);
			Path file = output.resolve(outputFile);

			try (InputStream in = u.toURL().openStream()) {
				Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
			}
			return file;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private WebUtils() {

	}

}