package com.lostmc.core.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {

	public static String formatDate() {
		TimeZone tz = TimeZone.getTimeZone("America/Sao_Paulo");
		Calendar calendar = GregorianCalendar.getInstance(tz);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(calendar.getTime());
	}
	
	public static String formatDifference(long time) {
		if (time == 0L) {
			return "";
		}

		long day = TimeUnit.SECONDS.toDays(time);
		long hours = TimeUnit.SECONDS.toHours(time) - day * 24L;
		long minutes = TimeUnit.SECONDS.toMinutes(time) - TimeUnit.SECONDS.toHours(time) * 60L;
		long seconds = TimeUnit.SECONDS.toSeconds(time) - TimeUnit.SECONDS.toMinutes(time) * 60L;

		StringBuilder sb = new StringBuilder();
		if (day > 0L) {
			sb.append(day).append(" ").append(day == 1L ? "dia" : "dias").append(" ");
		}
		if (hours > 0L) {
			sb.append(hours).append(" ").append(hours == 1L ? "hora" : "horas").append(" ");
		}
		if (minutes > 0L) {
			sb.append(minutes).append(" ").append(minutes == 1L ? "minuto" : "minutos").append(" ");
		}
		if (seconds > 0L) {
			sb.append(seconds).append(" ").append(seconds == 1L ? "segundo" : "segundos");
		}
		String diff = sb.toString();

		return diff.isEmpty() ? "0 " + "segundo" : diff;
	}

	public static String getTime(long expire) {
		String string = formatDifference((expire - System.currentTimeMillis()) / 1000L);
		if (string == null || string.isEmpty()) {
			string = "0 segundo";
		}
		return string;
	}

	public static long parseDateDiff(String time) throws Exception {
		return parseDateDiff(time, true);
	}

	public static long parseDateDiff(String time, boolean future) throws Exception {
		Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
		Matcher m = timePattern.matcher(time);
		int years = 0;
		int months = 0;
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		boolean found = false;
		while (m.find()) {
			if (m.group() == null || m.group().isEmpty()) {
				continue;
			}

			for (int i = 0; i < m.groupCount(); i++) {
				if (m.group(i) != null && !m.group(i).isEmpty()) {
					found = true;
					break;
				}
			}

			if (found) {
				if (m.group(1) != null && !m.group(1).isEmpty()) {
					years = Integer.parseInt(m.group(1));
				}

				if (m.group(2) != null && !m.group(2).isEmpty()) {
					months = Integer.parseInt(m.group(2));
				}

				if (m.group(3) != null && !m.group(3).isEmpty()) {
					weeks = Integer.parseInt(m.group(3));
				}

				if (m.group(4) != null && !m.group(4).isEmpty()) {
					days = Integer.parseInt(m.group(4));
				}

				if (m.group(5) != null && !m.group(5).isEmpty()) {
					hours = Integer.parseInt(m.group(5));
				}

				if (m.group(6) != null && !m.group(6).isEmpty()) {
					minutes = Integer.parseInt(m.group(6));
				}

				if (m.group(7) != null && !m.group(7).isEmpty()) {
					seconds = Integer.parseInt(m.group(7));
				}

				break;
			}
		}
		if (!found) {
			throw new Exception("Illegal Date");
		}

		if (years > 100) {
			throw new Exception("Illegal Date");
		}

		Calendar c = new GregorianCalendar();
		if (years > 0) {
			c.add(Calendar.YEAR, years * (future ? 1 : -1));
		}
		if (months > 0) {
			c.add(Calendar.MONTH, months * (future ? 1 : -1));
		}
		if (weeks > 0) {
			c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
		}
		if (days > 0) {
			c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
		}
		if (hours > 0) {
			c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
		}
		if (minutes > 0) {
			c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
		}
		if (seconds > 0) {
			c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
		}
		return c.getTimeInMillis();
	}
}
