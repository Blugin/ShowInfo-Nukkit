package nukkitplugin.showinfo.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.scheduler.PluginTask;
import nukkitplugin.showinfo.ShowInfo;
import nukkitplugin.showinfo.event.SendInfoEvent;

public class SendInfoTask extends PluginTask<ShowInfo> {
	public SendInfoTask(ShowInfo owner) {
		super(owner);
	}

	@Override
	public void onRun(int currentTick) {
		String format;
		int push = this.owner.getPeriod();
		if (push == 0) {
			format = this.owner.getFormat();
		} else {
			StringBuilder pushBuilder = new StringBuilder();
			for (int i = 0; i < Math.abs(push); i++)
				pushBuilder.append(" ");
			if (push > 0)
				format = pushBuilder.append(this.owner.getFormat().replace("\n", "\n" + pushBuilder.toString())).toString();
			else
				format = pushBuilder.insert(0, this.owner.getFormat().replace("\n", pushBuilder.toString() + "\n")).append(pushBuilder.toString()).toString();
		}
		Server.getInstance().getOnlinePlayers().values().stream().filter(player -> (!this.owner.getIgnorePlayers().contains(player.getName().toLowerCase()) && player.isAlive() && player.spawned)).forEach(player -> {
			SendInfoEvent event = new SendInfoEvent(player, format);
			Server.getInstance().getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				String info = event.getInfo();
				if (event.isReplace()) {
					Pattern pattern = Pattern.compile("\\{([a-zA-Z]+)\\}");
					Matcher matcher = pattern.matcher(info);
					while (matcher.find() == true) {
						switch (matcher.group(1).toString().toLowerCase()) {
							case "players":
								info = format.replace(matcher.group(0), String.valueOf(this.owner.getServer().getOnlinePlayers().size()));
								break;
							case "maxplayers":
								info = format.replace(matcher.group(0), String.valueOf(this.owner.getServer().getMaxPlayers()));
								break;
						}
					}
					matcher = pattern.matcher(info);
					while (matcher.find() == true) {
						String replace;
						switch (matcher.group(1).toLowerCase()) {
							case "player":
								replace = player.getName();
								break;
							case "iplayer":
								replace = player.getName().toLowerCase();
								break;
							case "displayName":
								replace = player.getDisplayName();
								break;
							case "nametag":
								replace = player.getNameTag();
								break;
							case "uuid":
								replace = player.getUniqueId().toString();
								break;
							case "health":
								replace = String.valueOf(player.getHealth());
								break;
							case "maxhealth":
								replace = String.valueOf(player.getMaxHealth());
								break;
							case "healthpercentage":
								replace = String.valueOf((int) (((double) player.getHealth()) / ((double) player.getMaxHealth()) * 100));
								break;
							case "x":
								replace = String.valueOf(NukkitMath.round(player.x, 1));
								break;
							case "y":
								replace = String.valueOf(NukkitMath.round(player.y, 1));
								break;
							case "z":
								replace = String.valueOf(NukkitMath.round(player.z, 1));
								break;
							case "yaw":
								replace = String.valueOf(player.yaw);
								break;
							case "pitch":
								replace = String.valueOf(player.pitch);
								break;
							case "world":
								replace = player.level.getFolderName();
								break;
							case "itemid":
								replace = String.valueOf(player.getInventory().getItemInHand().getId());
								break;
							case "itemdamage":
								replace = String.valueOf(player.getInventory().getItemInHand().getDamage());
								break;
							case "itemname":
								replace = player.getInventory().getItemInHand().getName();
								break;
							case "level":
								replace = String.valueOf(player.getExperienceLevel());
								break;
							case "exp":
								replace = String.valueOf(player.getExperience());
								break;
							case "totalexp":
								int totalExp = player.getExperience();
								for (int i = 0; i < player.getExperienceLevel(); i++)
									totalExp += Player.calculateRequireExperience(i);
								replace = String.valueOf(totalExp);
								break;
							case "exppercentage":
								replace = String.valueOf(NukkitMath.round(((float) player.getExperience()) / Player.calculateRequireExperience(player.getExperienceLevel()) * 100, 1));
								break;
							case "skinmodel":
								replace = player.getSkin().getModel();
								break;
							default:
								continue;
						}
						info = info.replace(matcher.group(0), replace);
					}
				}
				player.sendPopup(info);
			}
		});
	}
}
