package com.scapelog.client.notification;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.primitives.Ints;
import com.scapelog.api.event.EventListener;
import com.scapelog.api.event.impl.GameMessageEvent;
import com.scapelog.api.ui.TimedNotification;
import com.scapelog.api.util.Components;
import com.scapelog.api.util.SettingsUtils;
import com.scapelog.client.config.Config;
import com.scapelog.client.event.EventDispatcher;
import com.scapelog.client.ui.component.TitleBar;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public final class AugmentedItemNotificationManager extends NotificationManager {
	private final static String CONFIG_KEY = "augmented_item_notification";

	private final Pattern LEVEL_UP_PATTERN = Pattern.compile("<col=ffff00>Congratulations! Your (.*) has gained a level! It is now level (.*)");
	private final int[] NOTIFIABLE_LEVELS = {
		10, 12
	};

	private final TitleBar titleBar;

	public AugmentedItemNotificationManager(TitleBar titleBar) {
		super("Augmented item levelup");
		this.titleBar = titleBar;
		registerListener();
	}

	@Override
	public Node getPanelContent() {
		VBox box = new VBox(10);
		Components.setPadding(box, 10);

		for (int level : NOTIFIABLE_LEVELS ) {
			box.getChildren().add(SettingsUtils.createCheckBoxSetting("Notify level " + level,
					selected -> Config.setBoolean(CONFIG_KEY, "level_" + level, selected), notifyLevel(level)));
		}

		ScrollPane scrollPane = new ScrollPane(box);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		return scrollPane;
	}

	private void registerListener() {
		EventDispatcher.registerListener(new EventListener<GameMessageEvent>(GameMessageEvent.class) {
			@Override
			public void eventExecuted(GameMessageEvent event) {
				if (event.getType() != 0 || event.getFlags() != 0) {
					return;
				}
				Matcher matcher = LEVEL_UP_PATTERN.matcher(event.getMessage());
				if (!matcher.find()) {
					return;
				}
				String item = matcher.group(1);
				int level = Ints.tryParse(matcher.group(2));

				if (!notifyLevel(level)) {
					return;
				}
				notification("Your " + item + " has leveled up to level " + level + "!", 15, TimeUnit.SECONDS);
			}
		});
	}

	private void notification(String message, int delay, TimeUnit unit) {
		Platform.runLater(() -> {
			TimedNotification notification = new TimedNotification(message);
			notification.show(titleBar.getContent(), delay, unit);
		});
	}

	private boolean notifyLevel(int level) {
		return Config.getBooleanOrAdd(CONFIG_KEY, "level_" + level, false);
	}

}