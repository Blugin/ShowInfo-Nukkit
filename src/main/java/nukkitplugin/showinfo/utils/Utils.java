package nukkitplugin.showinfo.utils;

import static java.util.regex.Pattern.matches;

import java.util.LinkedHashMap;
import static java.util.regex.Pattern.compile;

public class Utils {
	public static boolean isNumber(String str) {
		return isNumber(str, true);
	}

	public static boolean isNumber(String str, boolean includeMinus) {
		return matches((includeMinus ? "(^-[0-9]*$)|" : "") + "(^[0-9]+$)", str);
	}

	public static int toInt(String str) {
		try {
			return (int) Double.parseDouble(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean toBoolean(String str) {
		switch (str.toLowerCase().trim()) {
			case "on":
			case "true":
			case "yes":
				return true;
		}
		return false;
	}

	public static LinkedHashMap<String, String> parseINI(String str) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		String[] block;
		for (String line : str.split("\n"))
			if (compile("[^#][a-zA-Z0-9\\-_\\.]*+=+[^\\r\\n]*").matcher(line).matches()) {
				block = line.split("=", -1);
				map.put(block[0], block[1].trim());
			}
		return map;
	}
}
