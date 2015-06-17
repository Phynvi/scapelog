package com.scapelog.client;

import com.scapelog.agent.Agent;
import com.scapelog.client.config.Config;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.client.event.ClientEventListener;
import com.scapelog.client.event.ClientEventReceiver;
import com.scapelog.client.event.impl.ClientLoadEvent;
import com.scapelog.client.event.impl.ClientReloadEvent;
import com.scapelog.client.event.impl.LoadingEvent;
import com.scapelog.client.loader.ClientLoader;
import com.scapelog.client.model.Language;
import com.scapelog.client.model.User;
import com.scapelog.client.model.WorldList;
import com.scapelog.client.plugins.PluginLoader;
import com.scapelog.client.ui.LoginWindow;
import com.scapelog.client.ui.UserInterface;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

import javax.swing.SwingUtilities;
import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class ScapeLog {
	private static User user;
	private static boolean agentEnabled;
	private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);
	public static final boolean debug = false;

	private UserInterface userInterface;

	public static void main(String[] args) {
		ScapeLog scapeLog = new ScapeLog();
		executor.submit(new ClientEventReceiver());

		try {
			Config.setup();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (scapeLog.userInterface != null) {
				scapeLog.userInterface.saveSize();
			}
		}));

//		if (debug) {
			scapeLog.startUI();
//		} else {
//			scapeLog.start();
//		}
	}

	private void start() {
		new JFXPanel(); // initialize toolkit
		Platform.runLater(() -> {
			try {
				LoginWindow loginWindow = new LoginWindow(this);
				loginWindow.start(new Stage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void startUI() {
		PluginLoader pluginLoader = new PluginLoader();

		userInterface = new UserInterface();
		userInterface.setup(pluginLoader);

		pluginLoader.load();
		pluginLoader.start();

		ClientEventDispatcher.registerListener(new ClientEventListener<ClientLoadEvent>(ClientLoadEvent.class) {
			@Override
			public void eventExecuted(ClientLoadEvent event) {
				Optional<WorldList> world = event.getWorld();
				Language language = event.getLanguage();

				executor.execute(() -> {
					try {
						ClientLoader clientLoader = ClientLoader.create(world, language);
						Applet applet = clientLoader.load();
						if (applet == null) {
							return;
						}

						Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
							Object source = e.getSource();
							if (source.getClass().getSuperclass().equals(Canvas.class)) {
								Canvas canvas = (Canvas) source;
								if (!canvas.hasFocus()) {
									canvas.requestFocus();
								}
							}
						}, AWTEvent.MOUSE_EVENT_MASK);

						SwingUtilities.invokeLater(() -> {
//							if (!debug)
							{
								ClientEventDispatcher.fireEvent(new LoadingEvent("Starting client..."));
								applet.setPreferredSize(new Dimension(1200, 800));
								applet.init();
								applet.start();
								userInterface.addApplet(applet);
							}
						});
						clientLoader = null;
						System.gc();
					} catch (Exception e) {
						e.printStackTrace();
						ClientEventDispatcher.fireEvent(new LoadingEvent("Failed to load client, cause: " + e.getMessage()));
					}
				});
			}
		});
		ClientEventDispatcher.registerListener(new ClientEventListener<ClientReloadEvent>(ClientReloadEvent.class) {
			@Override
			public void eventExecuted(ClientReloadEvent event) {
				userInterface.removeApplet();
				loadClient();
			}
		});

//		if (!debug)
		{
			loadClient();
		}
	}

	private void loadClient() {
		// todo: load world from preferences
		ClientEventDispatcher.fireEvent(new ClientLoadEvent(Optional.empty(), Language.getSavedLanguage()));
	}

	public static ScheduledExecutorService getExecutor() {
		return executor;
	}

	public static void enableAgent() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement source = stackTrace[2];
		boolean verifiedSource = source.getClassName().equals(Agent.class.getName());
		if (verifiedSource) {
			agentEnabled = true;
		}
	}

	public static boolean isAgentEnabled() {
		return agentEnabled;
	}

	public static User getUser() {
		return user;
	}

	public static void setUser(User user) {
		if (ScapeLog.user != null) {
			return;
		}
		ScapeLog.user = user;
	}

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");

		String osName = System.getProperty("os.name", "generic").toLowerCase();
		if (osName.startsWith("mac") || osName.startsWith("darwin")) {
			// manually load libjawt.dylib into vm, needed since Java 7
			System.out.println("Attempting to load jawt");
			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
				try {
					System.loadLibrary("jawt");
					System.out.println("\tLoaded successfully");
				} catch (UnsatisfiedLinkError e) {
					e.printStackTrace();
					// catch and ignore an already loaded in another classloader
					// exception, as vm already has it loaded
				}
				return null;
			});
		}
	}

}
