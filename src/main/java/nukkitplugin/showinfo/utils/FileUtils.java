package nukkitplugin.showinfo.utils;

import static java.lang.String.valueOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static nukkitplugin.showinfo.ShowInfo.getInstance;
import static nukkitplugin.showinfo.utils.JsonUtils.*;
import static nukkitplugin.showinfo.utils.Translation.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import cn.nukkit.Server;
import cn.nukkit.permission.Permission;
import nukkitplugin.showinfo.command.ShowInfoCommand;
import nukkitplugin.showinfo.command.subcommand.SubCommandData;

public class FileUtils {
	public static void mkdirs() {
		mkdirs("");
	}

	public static void mkdirs(String str) {
		new File(getInstance().getDataFolder() + "/" + str).mkdirs();
	}

	public static File getConfigFile(String fileName) {
		mkdirs("config");
		return new File(getInstance().getDataFolder() + "/config/" + fileName);
	}

	public static String getConfigResource(String fileName) {
		File file = getConfigFile(fileName);
		getInstance().saveResource("config/" + fileName, fileName, false);
		return loadFile(file);
	}

	public static File getDataFile(String fileName) {
		mkdirs("data");
		return new File(getInstance().getDataFolder() + "/data/" + fileName);
	}

	public static String getDataResource(String fileName) {
		File file = getDataFile(fileName);
		getInstance().saveResource("data/" + fileName, fileName, false);
		return loadFile(file);
	}

	public static void loadSetting() {
		try {
			JSONObject jsonSetting = parseObjectFromResource("setting.json");
			load((String) jsonSetting.get("language"));
			getInstance().setEnable((boolean) jsonSetting.get("enable"));
			getInstance().setPeriod((int) ((long) jsonSetting.get("indentlevel")));
			getInstance().setPeriod((int) ((long) jsonSetting.get("period")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void saveSetting() {
		try {
			JSONObject jsonSetting = new JSONObject() {
				{
					put("language", getLang());
					put("enable", getInstance().getEnable());
					put("indentlevel", getInstance().getIndent());
					put("period", getInstance().getPeriod());
				}
			};
			saveFile(getDataFile("/setting.json"), jsonSetting);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadPermissions() {
		try {
			JSONObject jsonPermissions = parseObjectFromResource("permission/commandpermission.json");
			getInstance().getDescription().getPermissions().stream().filter(permission -> jsonPermissions.containsKey(permission.getName())).forEach(permission -> permission.setDefault(Permission.getByName(valueOf(jsonPermissions.get(permission.getName())))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadCommandSetting() {
		try {
			JSONObject jsonCommandSetting = parseObjectFromResource("command/maincommand.json");
			String command = (String) jsonCommandSetting.get("command");
			JSONArray jsonAliases = (JSONArray) jsonCommandSetting.get("aliases");
			String[] aliases = new String[jsonAliases.size()];
			for (int i = 0; i < jsonAliases.size(); i++)
				aliases[i] = ((String) jsonAliases.get(i)).toLowerCase();
			JSONObject jsonSubCommandSetting = parseObjectFromResource("command/subcommand.json");
			HashMap<String, SubCommandData> subCommands = new HashMap<String, SubCommandData>();
			new ArrayList<String>() {
				{
					add("on");
					add("off");
					add("enable");
					add("disable");
					add("push");
					add("period");
					add("reload");
					add("save");
					add("reset");
				}
			}.forEach(subCommandName -> {
				JSONObject jsonSubCommandData = (JSONObject) jsonSubCommandSetting.get(subCommandName);
				String subCommand = (String) jsonSubCommandData.get("command");
				JSONArray jsonSubCommandAliases = (JSONArray) jsonSubCommandData.get("aliases");
				ArrayList<String> subCommandAliases = new ArrayList<String>();
				for (int i = 0; i < jsonSubCommandAliases.size(); i++)
					subCommandAliases.add(((String) jsonSubCommandAliases.get(i)).toLowerCase());
				subCommands.put(subCommandName, new SubCommandData(subCommand, subCommandAliases));;
			});
			Server.getInstance().getCommandMap().register(command, new ShowInfoCommand(command, aliases, subCommands));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadIgnorePlayers() {
		try {
			ArrayList<String> ignorePlayers = new ArrayList<String>();
			for (Object ignorePlayer : parseArrayFromResource("ignoreplayers.json"))
				ignorePlayers.add((String) ignorePlayer);
			getInstance().setIgnorePlayers(ignorePlayers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void saveIgnorePlayers() {
		try {
			JSONArray jsonData = new JSONArray();
			getInstance().getIgnorePlayers().forEach(jsonData::add);
			saveFile(getDataFile("ignoreplayers.json"), jsonData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadFormat() {
		try {
			getInstance().setFormat(loadFile(getDataFile("format.txt")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveFormat() {
		try {
			saveFile(getDataFile("format.txt"), getInstance().getFormat());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String loadFile(File file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8));
			String temp;
			StringBuilder stringBuilder = new StringBuilder();
			while ((temp = reader.readLine()) != null)
				stringBuilder.append(temp).append("\n");
			reader.close();
			return stringBuilder.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean saveFile(File file, JSONObject content) {
		return saveFile(file, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(content.toJSONString())));
	}

	public static boolean saveFile(File file, JSONArray content) {
		return saveFile(file, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(content.toJSONString())));
	}

	public static boolean saveFile(File file, String content) {
		try {
			if (file.exists() || file.createNewFile()) {
				InputStream contentStream = new ByteArrayInputStream(content.getBytes(UTF_8));
				FileOutputStream saveStream = new FileOutputStream(file);
				int length;
				byte[] buffer = new byte[1024];
				while ((length = contentStream.read(buffer)) != -1)
					saveStream.write(buffer, 0, length);
				saveStream.close();
				contentStream.close();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
