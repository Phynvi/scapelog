package com.scapelog.client.notification;

import com.scapelog.api.ui.TimedNotification;
import com.scapelog.api.util.Components;
import com.scapelog.api.util.SettingsUtils;
import com.scapelog.api.util.Utilities;
import com.scapelog.client.ScapeLog;
import com.scapelog.client.config.Config;
import com.scapelog.client.event.ClientEventDispatcher;
import com.scapelog.client.event.ClientEventListener;
import com.scapelog.client.event.impl.ClientWindowInitializedEvent;
import com.scapelog.client.ui.DecoratedFrame;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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

		for (Clan clan : Clan.values()) {
			box.getChildren().add(SettingsUtils.createCheckBoxSetting(clan.getName(), clan::setTracked, clan.isTracked()));
		}

		ScrollPane scrollPane = new ScrollPane(box);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		return scrollPane;
	}

	private void schedule(DecoratedFrame frame) {
		long nextHour = millisToNextHour(Calendar.getInstance());
		long delay = TimeUnit.MINUTES.toMillis(5);
		ScapeLog.getExecutor().execute(() -> checkVoice(frame));
		ScapeLog.getExecutor().scheduleWithFixedDelay(() -> checkVoice(frame), nextHour + delay, 3600000, TimeUnit.MILLISECONDS);
	}

	private void checkVoice(DecoratedFrame frame) {
		if (!showNotifications()) {
			return;
		}
		try {
			Document document = Jsoup.connect("https://twitter.com/JagexClock").get();
			Elements elements = document.select(".stream-item > .tweet > .content");
			long maxTweetAge = TimeUnit.MINUTES.toMillis(10);
			for (Element element : elements) {
				Elements timestamps = element.getElementsByClass("_timestamp");

				String timestamp = timestamps.first().attr("data-time-ms");
				long time = Long.parseLong(timestamp);
				long age = System.currentTimeMillis() - time;

				if (age > maxTweetAge) {
					continue;
				}

				Element tweetText = element.getElementsByClass("tweet-text").first();
				String tweet = tweetText.text();

				Clan[] clans = parseClans(tweet);
				if (clans[0].isTracked() || clans[1].isTracked()) {
					notify(frame, clans);
				}
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void notify(DecoratedFrame frame, Clan[] clans) {
		Platform.runLater(() -> {
			TimedNotification notification = new TimedNotification("Voice of Seren is now " + clans[0].getName() + "/" + clans[1].getName());
			notification.show(frame.getTitleBar().getContent(), 10, TimeUnit.SECONDS);
		});
	}

	private Clan[] parseClans(String tweet) {
		// todo: parse clans from tweet
		return new Clan[] { Clan.TRAHAEARN, Clan.IORWERTH };
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

	enum Clan {
		AMLODD, CADARN, CRWYS, HEFIN, IORWERTH, ITHELL, MEILYR, TRAHAEARN;

		public String getName() {
			return Utilities.capitalize(name());
		}

		private String getConfigName() {
			return "track_" + getName().toLowerCase();
		}

		private void setTracked(boolean selected) {
			Config.setBoolean("vos", getConfigName(), selected);
		}

		public boolean isTracked() {
			return Config.getBooleanOrAdd("vos", getConfigName(), false);
		}

	}

}