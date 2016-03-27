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

}
