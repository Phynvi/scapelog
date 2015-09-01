package com.scapelog.client.model;

import com.scapelog.api.util.Utilities;
import com.scapelog.client.config.Config;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VoiceOfSeren {
	private static final Pattern VOICE_MATCHER = Pattern.compile("The Voice of Seren is now active in the (.*?) and (.*?) districts.");

	private static SimpleObjectProperty<Clan[]> currentVoice = new SimpleObjectProperty<>();
	private static ObservableList<Clan[]> recentVoices = FXCollections.observableArrayList();

	private static LocalTime lastCheck = null;

	public static SimpleObjectProperty<Clan[]> getCurrentVoice() {
		if (canCheck()) {
			updateVoices();
		}
		return currentVoice;
	}

	public static ObservableList<Clan[]> getRecentVoices() {
		if (canCheck()) {
			updateVoices();
		}
		return recentVoices;
	}

	private static boolean canCheck() {
		LocalTime now = LocalTime.now();
		return lastCheck == null || lastCheck.getHour() != now.getHour();
	}

	private static void updateVoices() {
		try {
			Document document = Jsoup.connect("https://twitter.com/JagexClock").get();
			Elements elements = document.select(".stream-item > .tweet > .content");
			long maxTweetAge = TimeUnit.MINUTES.toMillis(10);
			int count = 0;
			recentVoices.clear();
			for (Element element : elements) {
				Elements timestamps = element.getElementsByClass("_timestamp");

				String timestamp = timestamps.first().attr("data-time-ms");
				long time = Long.parseLong(timestamp);
				long age = System.currentTimeMillis() - time;

				Element tweetText = element.getElementsByClass("tweet-text").first();
				String tweet = tweetText.text();

				Optional<Clan[]> clans = parseClans(tweet);
				if (count == 0) {
					currentVoice.setValue(clans.get());
				} else {
					clans.ifPresent(recentVoices::add);
				}
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		lastCheck = LocalTime.now();
	}

	private static Optional<Clan[]> parseClans(String tweet) {
		Matcher matcher = VOICE_MATCHER.matcher(tweet);
		if (matcher.find()) {
			Clan[] clans = new Clan[2];
			clans[0] = Clan.getClan(matcher.group(1));
			clans[1] = Clan.getClan(matcher.group(2));
			return Optional.of(clans);
		}
		return Optional.empty();
	}

	public static enum Clan {
		AMLODD(0), CADARN(1), CRWYS(2), HEFIN(3), IORWERTH(4), ITHELL(5), MEILYR(6), TRAHAEARN(7);

		private Image image;

		private final int id;

		Clan(int id) {
			this.id = id;
		}

		public String getName() {
			return Utilities.capitalize(name());
		}

		private String getConfigName() {
			return "track_" + getName().toLowerCase();
		}

		public void setTracked(boolean selected) {
			Config.setBoolean("vos", getConfigName(), selected);
		}

		public boolean isTracked() {
			return Config.getBooleanOrAdd("vos", getConfigName(), false);
		}

		public Image getImage() {
			if (image == null) {
				image = new Image(VoiceOfSeren.class.getResourceAsStream("/img/vos/" + id + ".png"));
			}
			return image;
		}

		public static Clan getClan(String name) {
			for (Clan clan : Clan.values()) {
				if (clan.getName().equals(name)) {
					return clan;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return getName();
		}
	}

}