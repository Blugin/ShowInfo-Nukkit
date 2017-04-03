package nukkitplugin.showinfo;

import static nukkitplugin.showinfo.utils.FileUtils.*;

import java.util.ArrayList;
import java.util.List;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.TaskHandler;
import nukkitplugin.showinfo.listener.SaveCommandListener;
import nukkitplugin.showinfo.task.SendInfoTask;

public class ShowInfo extends PluginBase {
	private static ShowInfo	instance;
	private TaskHandler		sendInfoTask;
	private String			format;
	private boolean			enable;
	private int				indent;
	private int				period;
	private List<String>	ignorePlayers;

	public static ShowInfo getInstance() {
		return ShowInfo.instance;
	}

	@Override
	public void onLoad() {
		ShowInfo.instance = this;
		loadAll();
	}

	@Override
	public void onEnable() {
		if (getEnable())
			taskStart();
		getServer().getPluginManager().registerEvents(new SaveCommandListener(), this);
	}

	@Override
	public void onDisable() {
		taskStop();
		saveAll();
	}

	public void loadAll() {
		mkdirs();
		saveDefaultData(false);
		loadSetting();
		loadPermissions();
		loadCommandSetting();
		loadIgnorePlayers();
		loadFormat();
	}

	public void saveAll() {
		mkdirs();
		saveSetting();
		saveIgnorePlayers();
		saveFormat();
	}

	public void saveDefaultData(boolean replace) {
		new ArrayList<String>() {
			{
				add("data/format.txt");
				add("data/ignoreplayers.json");
				add("data/setting.json");
				add("config/command/maincommand.json");
				add("config/command/subcommand.json");
				add("config/lang/eng.ini");
				add("config/lang/kor.ini");
				add("config/permission/commandpermission.json");
			}
		}.forEach(fileName -> saveResource("config/" + fileName, fileName, replace));
	}

	public boolean isTaskStop() {
		return (sendInfoTask == null || sendInfoTask.isCancelled());
	}

	public void taskStart() {
		if (isTaskStop() != true)
			taskStop();
		sendInfoTask = getServer().getScheduler().scheduleDelayedRepeatingTask(new SendInfoTask(this), period, period);
	}

	public void taskStop() {
		if (isTaskStop() != true) {
			sendInfoTask.cancel();
			sendInfoTask = null;
		}
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean getEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public int getIndent() {
		return indent;
	}

	public void setIndent(int indent) {
		this.indent = indent;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public List<String> getIgnorePlayers() {
		return ignorePlayers;
	}

	public void setIgnorePlayers(List<String> ignorePlayers) {
		this.ignorePlayers = ignorePlayers;
	}
}
