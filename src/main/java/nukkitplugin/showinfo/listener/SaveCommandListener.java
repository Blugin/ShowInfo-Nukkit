package nukkitplugin.showinfo.listener;

import static cn.nukkit.event.EventPriority.HIGHEST;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.server.ServerCommandEvent;
import nukkitplugin.showinfo.ShowInfo;
import nukkitplugin.showinfo.utils.Translation;

public class SaveCommandListener implements Listener {
	@EventHandler(priority = HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().toLowerCase().startsWith("/save-all"))
			this.saveAll((CommandSender) event.getPlayer());

	}

	@EventHandler(priority = HIGHEST)
	public void onServerCommandEvent(ServerCommandEvent event) {
		if (event.getCommand().toLowerCase().startsWith("save-all"))
			this.saveAll((CommandSender) event.getSender());

	}

	public void saveAll(CommandSender sender) {
		Command command = Server.getInstance().getCommandMap().getCommand("save-all");
		if (command != null && command.testPermissionSilent(sender)) {
			ShowInfo.getInstance().saveAll();
			Server.getInstance().getLogger().notice(Translation.successedTranslate("commands.save.success"));
		}
	}
}
