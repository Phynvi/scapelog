package com.scapelog.api.util;

import java.util.concurrent.TimeUnit;

public final class Utilities {

	private Utilities() {

	}

	public static String format(long value) {
		return String.format("%,d", value);
	}

	public static String formatTime(long elapsed) {
		long time = System.currentTimeMillis() - elapsed;
		long hours = TimeUnit.MILLISECONDS.toHours(time);
		time -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
		time -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
		return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
	}

	public static String withSuffix(long count) {
		if (count < 1000)
			return Long.toString(count);
		int exp = (int) (Math.log(count) / Math.log(1000));
		return String.format("%.1f%c", count / Math.pow(1000, exp), "kMGTPE".charAt(exp-1));
	}

	public static String capitalize(String str) {
		char[] chars = str.toCharArray();
		boolean sentenceStart = true;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (sentenceStart) {
				if (c >= 'a' && c <= 'z') {
					chars[i] -= 0x20;
					sentenceStart = false;
				} else if (c >= 'A' && c <= 'Z') {
					sentenceStart = false;
				}
			} else {
				if (c >= 'A' && c <= 'Z') {
					chars[i] += 0x20;
				}
			}
			if (c == '.' || c == '!' || c == '?') {
				sentenceStart = true;
			}
		}
		return new String(chars, 0, chars.length);
	}

}