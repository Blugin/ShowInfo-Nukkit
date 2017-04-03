package nukkitplugin.showinfo.utils;

import static nukkitplugin.showinfo.utils.FileUtils.getConfigResource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonUtils {
	public static JSONArray parseArrayFromResource(String str) {
		try {
			return (JSONArray) new JSONParser().parse(getConfigResource("command/subcommand.json"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject parseObjectFromResource(String str) {
		try {
			return (JSONObject) new JSONParser().parse(getConfigResource("command/subcommand.json"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
