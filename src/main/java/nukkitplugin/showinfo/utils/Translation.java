package nukkitplugin.showinfo.utils;

import static nukkitplugin.showinfo.ShowInfo.getInstance;
import static nukkitplugin.showinfo.utils.FileUtils.loadFile;
import static nukkitplugin.showinfo.utils.Utils.parseINI;

import java.io.File;
import java.util.LinkedHashMap;

import cn.nukkit.Server;

public class Translation {
	protected static LinkedHashMap<String, String>	langs;
	private static String							lang	= "default";

	public static void load(String lang) {
		Translation.lang = lang;
		Translation.lang = lang.equalsIgnoreCase("default") ? Server.getInstance().getLanguage().getLang() : lang.toLowerCase();
		File file = new File(getInstance().getDataFolder() + "/lang/" + lang + ".ini");
		if (file.exists() && file.isFile())
			Translation.langs = parseINI(loadFile(file));
		else
			getInstance().getLogger().error("Error at find language config file.");
	}

	public static String translate(String langText) {
		return translate(langText, new String[] {});
	}

	public static String translate(String langText, String param) {
		return translate(langText, new String[] { param });
	}

	public static String translate(String langText, String[] params) {
		String text = langs.getOrDefault(langText, "");
		if (text.equals(""))
			return null;
		else
			for (int i = 0; i < params.length; i++)
				text = text.replace("{%" + i + "}", params[i]);
		return text.replace("\\n", "\n");
	}

	public static String successedTranslate(String langText) {
		return successedTranslate(langText, new String[] {});
	}

	public static String successedTranslate(String langText, String param) {
		return successedTranslate(langText, new String[] { param });
	}

	public static String successedTranslate(String langText, String[] params) {
		return translate("colors.success") + translate("prefix") + " " + translate(langText, params);
	}

	public static String failedTranslate(String langText) {
		return failedTranslate(langText, new String[] {});
	}

	public static String failedTranslate(String langText, String param) {
		return failedTranslate(langText, new String[] { param });
	}

	public static String failedTranslate(String langText, String[] params) {
		return translate("colors.failed") + translate("prefix") + " " + translate(langText, params);
	}

	public static String getLang() {
		return lang;
	}
}
