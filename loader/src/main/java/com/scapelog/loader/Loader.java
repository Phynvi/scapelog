package com.scapelog.loader;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public final class Loader {
	private final int VERSION = 5;
	private final OperatingSystem operatingSystem = OperatingSystem.getOperatingSystem();

	private final String dataDirectory = System.getProperty("user.home") + "/.scapelog";
	private String libDirectory = dataDirectory + "/lib";
	private final String javaDirectory = libDirectory + "/java";
	private final List<Dependency> dependencies = new ArrayList<Dependency>();

	private JFrame frame;
	private JLabel progressLabel;
	private JProgressBar progressBar;

	private boolean printOutput = false;
	private boolean noChecksumCheck = false;
	private boolean noMods = false;
	private boolean forcePortableJava = false;
	private boolean testingFiles = false;

	private List<BootFlag> bootFlags = new ArrayList<BootFlag>();

	public static void main(String[] args) throws Exception {
		Loader loader = new Loader();
		loader.loadBootFlags();

		if (args.length >= 1) {
			for (String arg : args) {
				for (BootFlag bootFlag : loader.bootFlags) {
					if (arg.equals(bootFlag.getFlag())) {
						bootFlag.getAction().run();
					}
				}
			}
		}

		loader.setPaths();

		loader.setupFrame();
		if (!loader.hasValidJava()) {
			if (loader.operatingSystem == OperatingSystem.GENERIC) {
				JOptionPane.showMessageDialog(null, "Please upgrade your Java to version 8 to use ScapeLog");
				System.exit(0);
				return;
			}
			int option = JOptionPane.showConfirmDialog(null, "ScapeLog requires Java 8 but we detected that you don't have it.\nChoose 'yes' if you want to automatically download a portable and compatible Java version or 'no' if you wish to upgrade yourself.", "Error", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.NO_OPTION) {
				System.exit(0);
				return;
			}
			loader.downloadJava();
		}
		try {
			loader.loadList();
			List<Dependency> missingDependencies = loader.checkFiles();
			if (missingDependencies.size() > 0 && !loader.noChecksumCheck) {
				loader.downloadFiles(missingDependencies);
			}
		} catch (Exception e) {
			String message = e.getMessage();
			if (e instanceof FileNotFoundException) {
				message = "404: " + message;
			}
			e.printStackTrace();
			loader.progressLabel.setText("Error: " + message);
			return;
		}
		loader.launch();
	}

	private void setPaths() {
		if (testingFiles) {
			libDirectory = dataDirectory + "/lib/testing";
		}
	}

	private void loadBootFlags() {
		bootFlags.add(new BootFlag("-help", "Prints this message", new Runnable() {
			@Override
			public void run() {
				printHelp();
				System.exit(0);
			}
		}));
		bootFlags.add(new BootFlag("-out", "Redirects output to the loader for debugging", new Runnable() {
			@Override
			public void run() {
				printOutput = true;
			}
		}));
		bootFlags.add(new BootFlag("-noupdate", "Checks for updates but does not download them", new Runnable() {
			@Override
			public void run() {
				noChecksumCheck = true;
			}
		}));
		bootFlags.add(new BootFlag("-nomods", "Launches the application without any modificating abilities", new Runnable() {
			@Override
			public void run() {
				noMods = true;
			}
		}));
		bootFlags.add(new BootFlag("-portablejava", "Force use the portable Java 8", new Runnable() {
			@Override
			public void run() {
				forcePortableJava = true;
			}
		}));
		bootFlags.add(new BootFlag("-testing", "Use the files in testing phase (not recommended)", new Runnable() {
			@Override
			public void run() {
				testingFiles = true;
			}
		}));
	}

	private void printHelp() {
		System.out.println("USAGE: java -jar ScapeLog.jar [OPTIONS]");
		System.out.println();
		System.out.println(String.format("%-20s%-15s", "Option", "Description"));
		for (BootFlag bootFlag : bootFlags) {
			System.out.println(String.format(" %-19s%s", bootFlag.getFlag(), bootFlag.getDescription()));
		}

		System.exit(0);
	}

	private void setupFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			/**/
		}
		frame = new JFrame("ScapeLog Launcher");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		progressLabel = new JLabel("Preparing to launch ScapeLog...");
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);

		progressLabel.setText("Preparing to start ScapeLog");
		progressBar.setString("");

		contentPanel.add(progressLabel, BorderLayout.CENTER);
		contentPanel.add(progressBar, BorderLayout.SOUTH);

		frame.setContentPane(contentPanel);
		frame.setSize(350, 75);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

	private boolean hasValidJava() {
		if (forcePortableJava && hasValidPortableJava()) {
			return true;
		}
		return hasValidJavaInstalled() || hasValidPortableJava();
	}

	private boolean hasValidPortableJava() {
		boolean installed = hasValidJavaInstalled();
		if (!installed) {
			File javaFolder = new File(javaDirectory);
			if (!javaFolder.exists()) {
				return false;
			}
			String executableName = OperatingSystem.getExecutable(operatingSystem);
			if (executableName == null) {
				System.out.println("unknown executable for " + operatingSystem);
				return false;
			}
			File executable = new File(javaFolder, executableName);
			return executable.isFile() && executable.exists();
		}
		return false;
	}

	private boolean hasValidJavaInstalled() {
		String specificationVersion = System.getProperty("java.specification.version");
		String[] versionParts = specificationVersion.split("\\.");
		int major = Integer.parseInt(versionParts[0]);
		int minor = Integer.parseInt(versionParts[1]);
		if (major >= 1 && minor >= 8) {
			return true;
		}
		if (operatingSystem == OperatingSystem.GENERIC) {
			return false;
		}
		return false;
	}

	private void downloadJava() {
		/* get download link */
		progressLabel.setText("Downloading Java...");
		String downloadLink = operatingSystem.getDownloadLink();

		/* create directory */
		File javaFolder = new File(javaDirectory);
		if (!javaFolder.exists()) {
			if(!javaFolder.mkdirs()) {
				System.out.println("Failed to create directory '" + javaFolder + "', please create it manually");
				System.exit(0);
			}
		}

		/* download archive */
		String output = javaFolder.getAbsolutePath() + "/java.tar.gz";
		try {
			download(downloadLink, "Java", output);
		} catch (Exception e) {
			e.printStackTrace();
			progressLabel.setText("Failed to download Java: " + e.getMessage());
			return;
		}

		/* extract archive */
		progressLabel.setText("Extracting Java...");
		try {
			TarInputStream tis = new TarInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(output))));
			TarEntry entry = tis.getNextEntry();
			String rootName = null;
			if (entry.isDirectory()) {
				rootName = entry.getName();
			}
			while((entry = tis.getNextEntry()) != null) {
				String name = entry.getName();
				if (rootName != null && name.indexOf(rootName) == 0) {
					name = name.substring(rootName.length());
				}

				File destination = new File(javaFolder, name);
				if (entry.isDirectory()) {
					boolean created = destination.mkdirs();
					if (!created) {
						System.out.println("Failed to create directory " + destination);
					}
				} else {
					int count;
					byte[] buffer = new byte[4096];
					FileOutputStream outputStream = new FileOutputStream(destination);
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, buffer.length);
					while ((count = tis.read(buffer, 0, buffer.length)) != -1) {
						bufferedOutputStream.write(buffer, 0, count);
					}
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					outputStream.close();
				}
			}
			tis.close();
		} catch (Exception e) {
			e.printStackTrace();
			progressLabel.setText("Failed to extract Java: " + e.getMessage());
			return;
		}

		/* delete archive */
		if(!new File(output).delete()) {
			System.out.println("Failed to delete Java archive from " + output);
		}
	}

	private void loadList() throws Exception {
		try {
			progressLabel.setText("Fetching file list...");
			String[] lines;
			try {
				lines = downloadToMemory(testingFiles ? "http://static.scapelog.com/test/checksums" : "http://static.scapelog.com/live/checksums");
			} catch (FileNotFoundException e) {
				throw new Exception("Remote file list not found, please try again later");
			}
			int loaderVersion = Integer.parseInt(lines[0]);
			if (VERSION != loaderVersion) {
				JOptionPane.showMessageDialog(null, "Your loader is out of date, please download the new version from our website.");
				System.exit(0);
			}
			for (int i = 1; i < lines.length; i++) {
				String line = lines[i];
				if (line.startsWith("#")) {
					continue;
				}
				String[] parts = line.split("\\$\\$");
				if (parts.length != 4) {
					continue;
				}
				int idx = 0;
				String type = parts[idx++];
				String fileName = parts[idx++];
				String checksum = parts[idx++];
				String url = parts[idx];
				dependencies.add(new Dependency(type, fileName, checksum, url));
			}
			progressLabel.setText("Fetching file list...done");
		} catch (Exception e) {
			progressLabel.setText("Failed to read file list, trying to launch without updating...");
			throw e;
		}
	}

	private List<Dependency> checkFiles() throws Exception {
		progressLabel.setText("Checking local files...");
		File dataFolder = new File(dataDirectory);
		if (!dataFolder.exists()) {
			boolean created = dataFolder.mkdir();
			if (!created) {
				throw new IOException("Failed to create data directory in '" + dataDirectory + "', please create it manually.");
			}
		}
		File libFolder = new File(libDirectory);
		if (!libFolder.exists()) {
			boolean created = libFolder.mkdirs();
			if (!created) {
				throw new IOException("Failed to create lib directory in '" + libDirectory + "', please create it manually.");
			}
		}
		File[] libFiles = libFolder.listFiles();
		if (libFiles == null) {
			libFiles = new File[0];
		}
		List<Dependency> missing = new ArrayList<Dependency>(dependencies);
		for (Dependency dependency : dependencies) {
			for (File file : libFiles) {
				if (file.isDirectory()) {
					continue;
				}
				String fileName = file.getName();
				if (!fileName.equals(dependency.getFileName())) {
					continue;
				}
				String localChecksum = getMD5Checksum(libDirectory + "/" + fileName);
				if (localChecksum.equals(dependency.getChecksum())) {
					missing.remove(dependency);
				}
			}
		}
		return missing;
	}

	private void downloadFiles(List<Dependency> missingDependencies) throws Exception {
		progressLabel.setText("Downloading files...");
		for (Dependency dependency : missingDependencies) {
			download(dependency.getUrl(), dependency.getFileName(), libDirectory + "/" + dependency.getFileName());
		}
	}

	private void launch() {
		progressLabel.setText("Launching...");

		String executable = OperatingSystem.getExecutable(operatingSystem);
		if (executable == null) {
			executable = "java";
		}
		if (!forcePortableJava && hasValidJavaInstalled()) {
			executable = System.getProperty("java.home") + "/" + executable;
		} else if (hasValidPortableJava()) {
			executable = javaDirectory + "/" + executable;
		}
		File executableFile = new File(executable);
		if (!executableFile.canExecute()) {
			if(!executableFile.setExecutable(true)) {
				System.out.println("Failed to make file '" + executable + "' executable, please do it manually and try again");
			}
		}

		String separator = operatingSystem.equals(OperatingSystem.WINDOWS) ? ";" : ":";
		String joinedDependencies = "";

		Dependency mainDependency = null;

		for (Dependency dependency : dependencies) {
			joinedDependencies += libDirectory + "/" + dependency.getFileName() + separator;
			if (dependency.getType().equals("main")) {
				mainDependency = dependency;
			}
		}
		if (joinedDependencies.endsWith(separator)) {
			joinedDependencies = joinedDependencies.substring(0, joinedDependencies.length() - 1);
		}

		try {
			String prepend = getCommandPrepend();

			List<String> commandParts = new ArrayList<String>();
			if (prepend != null) {
				commandParts.add(prepend);
			}
			commandParts.add(executable);
			if (mainDependency != null && !noMods) {
				commandParts.add("-javaagent:" + libDirectory + "/" + mainDependency.getFileName());
			}
			commandParts.add("-cp");
			commandParts.add(joinedDependencies);
			commandParts.add("com.scapelog.client.ScapeLog"); // todo: main class from somewhere

			System.out.println(commandParts);

			if (printOutput) {
				frame.dispose();
				ProcessBuilder builder = new ProcessBuilder(commandParts);
				builder.redirectErrorStream(true);
				Process process = builder.start();
				String line;
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				System.out.println("====================");
				System.out.println("\tOutput printing enabled - the loader will remain running while the client runs");
				System.out.println("\tThe -out flag is only recommended for debugging purposes!");
				System.out.println("====================");
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
			} else {
				Runtime.getRuntime().exec(commandParts.toArray(new String[commandParts.size()]));
			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void download(String url, String file, String out) throws Exception {
		progressBar.setIndeterminate(false);
		HttpURLConnection connection = getConnection(url);
		connection.connect();
		Map<String, List<String>> headers = connection.getHeaderFields();
		while (isRedirected(headers)) {
			url = headers.get("Location").get(0);
			connection = getConnection(url);
			headers = connection.getHeaderFields();
		}

		long totalLength = connection.getContentLength();
		long downloaded = 0;

		InputStream input = connection.getInputStream();
		FileOutputStream output = new FileOutputStream(out);
		byte[] tmp = new byte[4096];
		int read;
		while ((read = input.read(tmp)) != -1) {
			downloaded += read;
			int percent = (int) (100 * downloaded / totalLength);
			progressBar.setString("Downloading " + file + " " + percent + "%");
			progressBar.setValue(percent);
			output.write(tmp, 0, read);
		}
		output.flush();
		output.close();
	}

	private HttpURLConnection getConnection(String url) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		setHeaders(url, connection);
		try {
			connection.connect();
		} catch (Exception e) {
			e.printStackTrace();
			if (url.startsWith("http://")) {
				throw new IllegalStateException("Download failed", e);
			}
			url = url.replace("https://", "http://");
			connection = getConnection(url);
		}
		return connection;
	}

	private String[] downloadToMemory(String url) throws Exception {
		HttpURLConnection connection = getConnection(url);
		Map<String, List<String>> headers = connection.getHeaderFields();
		while (isRedirected(headers)) {
			url = headers.get("Location").get(0);
			connection = getConnection(url);
			headers = connection.getHeaderFields();
		}
		InputStream input = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line;
		List<String> lines = new ArrayList<String>();
		while((line = reader.readLine()) != null) {
			lines.add(line);
		}
		reader.close();
		input.close();
		connection.disconnect();
		return lines.toArray(new String[lines.size()]);
	}

	private void setHeaders(String file, HttpURLConnection connection) {
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
		if (file.equals("Java")) {
			connection.setRequestProperty("Cookie", "oraclelicense=accept-securebackup-cookie");
		}
	}

	private boolean isRedirected(Map<String, List<String>> header) {
		for (String hv : header.get(null)) {
			if (hv.contains("301") || hv.contains("302"))
				return true;
		}
		return false;
	}

	private String getMD5Checksum(String filename) throws Exception {
		byte[] bytes = createChecksum(filename);
		String result = "";
		for (byte b : bytes) {
			result += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	private byte[] createChecksum(String filename) throws Exception {
		InputStream is = new FileInputStream(filename);
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;
		do {
			numRead = is.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		is.close();
		return complete.digest();
	}

	private String getCommandPrepend() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(dataDirectory + "/prepend"));
			String prepend = reader.readLine();
			reader.close();
			return prepend;
		} catch (Exception e) {
			return null;
		}
	}

}