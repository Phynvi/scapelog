package com.scapelog.api.util;

import java.util.concurrent.TimeUnit;

public final class TimeUtils {

	private static String format(long elapsed, boolean hours) {
		final long hr = TimeUnit.MILLISECONDS.toHours(elapsed);
		final long min = TimeUnit.MILLISECONDS.toMinutes(elapsed - TimeUnit.HOURS.toMillis(hr));
		final long sec = TimeUnit.MILLISECONDS.toSeconds(elapsed - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
		return hours ? String.format("%02d:%02d:%02d", hr, min, sec) : String.format("%02d:%02d", min, sec);
	}

	public static String formatMinutes(final long elapsed) {
		return format(elapsed, false);
	}

	public static String formatHours(long elapsed) {
		return format(elapsed, true);
	}

	private TimeUtils() {

	}

}