package io.jpress.utils;

public class StringUtils {

	public static boolean areNotEmpty(String... strings) {
		if (strings == null || strings.length == 0)
			return false;

		for (String string : strings) {
			if (string == null || "".equals(string)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotEmpty(String string) {
		return string != null && !string.equals("");
	}

	public static boolean areNotBlank(String... strings) {
		if (strings == null || strings.length == 0)
			return false;

		for (String string : strings) {
			if (string == null || "".equals(string.trim())) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String string) {
		return string != null && !string.trim().equals("");
	}

	public static long toLong(String value, Long defaultValue) {
		try {
			if (value == null || "".equals(value.trim()))
				return defaultValue;
			value = value.trim();
			if (value.startsWith("N") || value.startsWith("n"))
				return -Long.parseLong(value.substring(1));
			return Long.parseLong(value);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public static int toInt(String value, int defaultValue) {
		try {
			if (value == null || "".equals(value.trim()))
				return defaultValue;
			value = value.trim();
			if (value.startsWith("N") || value.startsWith("n"))
				return -Integer.parseInt(value.substring(1));
			return Integer.parseInt(value);
		} catch (Exception e) {
		}
		return defaultValue;
	}

}
