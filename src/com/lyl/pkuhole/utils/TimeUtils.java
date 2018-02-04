package com.lyl.pkuhole.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

	/**
	 * Format the timestamp.
	 */
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String timeFormatter(long timestamp) {
		String raw = dateFormat.format(new Date(timestamp * 1000));
		String relative = relativeTimeFormatter(timestamp);
		return String.format("%s (%s)", raw, relative);
	}

	public static String relativeTimeFormatter(long timestamp) {
		long diff = System.currentTimeMillis() / 1000 - timestamp;
		if (diff >= 60 * 60 * 24 * 365)
			return String.format("%d年前", diff / (60 * 60 * 24 * 365));
		else if (diff >= 60 * 60 * 24)
			return String.format("%d天前", diff / (60 * 60 * 24));
		else if (diff >= 60 * 60)
			return String.format("%d小时前", diff / (60 * 60));
		else if (diff >= 60)
			return String.format("%d分钟前", diff / 60);
		else
			return String.format("%d秒前", diff);
	}
}
