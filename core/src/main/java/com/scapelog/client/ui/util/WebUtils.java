package com.scapelog.client.ui.util;

import com.goebl.david.Response;
import com.goebl.david.Webb;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

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

	public static String readPage(String baseUrl, String page) {
		Webb webb = Webb.create();
		webb.setBaseUri(baseUrl);
		webb.setFollowRedirects(true);
		Response<String> response = webb.get(page).connectTimeout(2000).readTimeout(2000).ensureSuccess().asString();
		return response.isSuccess() ? response.getBody() : null;
	}

	public static List<String> readLines(String baseUrl, String page) {
		String body = readPage(baseUrl, page);
		String[] lines = body.split("\n");
		return Arrays.asList(lines);
	}

	private WebUtils() {

	}

}