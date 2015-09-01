package com.scapelog.client.notification;

import com.scapelog.api.ui.TimedNotification;
import com.scapelog.api.util.Components;
import com.scapelog.api.util.SettingsUtils;
import com.scapelog.client.ScapeLog;
import com.scapelog.client.config.Config;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.client.event.ClientEventListener;
import com.scapelog.client.event.impl.ClientWindowInitializedEvent;
import com.scapelog.client.model.VoiceOfSeren;
import com.scapelog.client.ui.DecoratedFrame;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public final class VosNotificationManager extends NotificationManager {

	public VosNotificationManager() {
		super("Voice of Seren");

		ClientEventDispatcher.registerListener(new ClientEventListener<ClientWindowInitializedEvent>(ClientWindowInitializedEvent.class) {
			@Override
			public void eventExecuted(ClientWindowInitializedEvent event) {
				schedule(event.getFrame());
			}
		});
	}

	@Override
	public Node getPanelContent() {
		VBox box = new VBox(10);
		Components.setPadding(box, 10);

		box.getChildren().addAll(
				SettingsUtils.createCheckBoxSetting("Show notifications", selected -> Config.setBoolean("vos", "show_notification", selected), showNotifications()),
				Components.createSpacer(),
				Components.createHeader("Tracked voices", "Select which voices you want to be notified for")
		);

		for (VoiceOfSeren.Clan clan : VoiceOfSeren.Clan.values()) {
			box.getChildren().add(SettingsUtils.createCheckBoxSetting(clan.getName(), clan::setTracked, clan.isTracked()));
		}

		ScrollPane scrollPane = new ScrollPane(box);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		return scrollPane;
	}

	private void schedule(DecoratedFrame frame) {
		long nextHour = millisToNextHour(Calendar.getInstance());
		long delay = TimeUnit.MINUTES.toMillis(1);
		ScapeLog.getExecutor().execute(() -> checkVoice(false, frame));
		ScapeLog.getExecutor().scheduleWithFixedDelay(() -> checkVoice(showNotifications(), frame), nextHour + delay, 3600000, TimeUnit.MILLISECONDS);
	}

	private void checkVoice(boolean notify, DecoratedFrame frame) {
		if (!notify) {
			return;
		}
		VoiceOfSeren.Clan[] current = VoiceOfSeren.getCurrentVoice().getValue();
		if (current[0].isTracked() || current[1].isTracked()) {
			notify(frame, current);
		}
	}

	private void notify(DecoratedFrame frame, VoiceOfSeren.Clan[] clans) {
		Platform.runLater(() -> {
			TimedNotification notification = new TimedNotification("Voice of Seren is now " + clans[0].getName() + "/" + clans[1].getName());
			notification.show(frame.getTitleBar().getContent(), 10, TimeUnit.SECONDS);
		});
	}

	private boolean showNotifications() {
		return Config.getBooleanOrAdd("vos", "show_notification", false);
	}

	private static long millisToNextHour(Calendar calendar) {
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		int millis = calendar.get(Calendar.MILLISECOND);
		int minutesToNextHour = 60 - minutes;
		int secondsToNextHour = 60 - seconds;
		int millisToNextHour = 1000 - millis;
		return minutesToNextHour * 60 * 1000 + secondsToNextHour * 1000 + millisToNextHour;
	}

}