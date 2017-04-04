package nukkitplugin.showinfo.command;

import static java.lang.String.valueOf;
import static java.util.Arrays.copyOfRange;
import static java.util.stream.Collectors.toList;
import static nukkitplugin.showinfo.utils.Translation.*;
import static nukkitplugin.showinfo.utils.Utils.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import nukkitplugin.showinfo.ShowInfo;
import nukkitplugin.showinfo.command.subcommand.PlayerSubCommand;
import nukkitplugin.showinfo.command.subcommand.SubCommand;
import nukkitplugin.showinfo.command.subcommand.SubCommandData;

public class ShowInfoCommand extends Command {
	private LinkedHashMap<String, SubCommand> subCommands = new LinkedHashMap<String, SubCommand>();

	public ShowInfoCommand(String name, String[] aliases, Map<String, SubCommandData> subCommands) {
		super(name);
		setPermission("command.showinfo");
		setAliases(aliases);
		registerSubCommands(subCommands);
		super.description = getUsage();
		super.usageMessage = getUsage();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!this.testPermission(sender)) {
			return true;
		} else if (args.length == 0) {
			sender.sendMessage(translate("commands.generic.usage", getUsage(sender)));
			return true;
		} else {
			SubCommand subCommand = getSubCommand(args[0]);
			if (subCommand == null) {
				sender.sendMessage(translate("commands.generic.notFound", getUsage(sender)));
			} else if (!subCommand.hasPermission(sender)) {
				sender.sendMessage(translate("commands.generic.permission"));
			} else {
				subCommand.run(sender, copyOfRange(args, 1, args.length));
			}
		}
		return true;
	}

	@Override
	public String getUsage() {
		return "/" + getLabel() + " <" + String.join(" | ", subCommands.values().stream().map(SubCommand::getName).collect(toList())) + ">";
	}

	public String getUsage(CommandSender sender) {
		return "/" + getLabel() + " <" + String.join(" | ", subCommands.values().stream().filter(subCommand -> subCommand.hasPermission(sender)).map(SubCommand::getName).collect(toList())) + ">";
	}

	public void registerSubCommands(Map<String, SubCommandData> subCommands) {
		ShowInfo plugin = ShowInfo.getInstance();
		registerSubCommand(new PlayerSubCommand(this, subCommands.get("on"), "command.showinfo.on") {
			public void execute(Player player, String[] args) {
				if (!plugin.getIgnorePlayers().contains((player.getName().toLowerCase())))
					player.sendMessage(failedTranslate("commands.on.failed"));
				else {
					plugin.getIgnorePlayers().remove(player.getName().toLowerCase());
					player.sendMessage(successedTranslate("commands.on.success"));
				}
			}
		});
		registerSubCommand(new PlayerSubCommand(this, subCommands.get("off"), "command.showinfo.off") {
			public void execute(Player player, String[] args) {
				if (plugin.getIgnorePlayers().contains(player.getName().toLowerCase()))
					player.sendMessage(failedTranslate("commands.off.failed"));
				else {
					plugin.getIgnorePlayers().add(player.getName().toLowerCase());
					player.sendMessage(successedTranslate("commands.off.success"));
				}
			}
		});
		registerSubCommand(new SubCommand(this, subCommands.get("enable"), "command.showinfo.enable") {
			public void execute(CommandSender sender, String[] args) {
				if (!plugin.isTaskStop())
					sender.sendMessage(failedTranslate("commands.enable.failed"));
				else {
					plugin.setEnable(true);
					plugin.taskStart();
					sender.sendMessage(successedTranslate("commands.enable.success"));
				}
			}
		});
		registerSubCommand(new SubCommand(this, subCommands.get("disable"), "command.showinfo.disable") {
			public void execute(CommandSender sender, String[] args) {
				if (plugin.isTaskStop())
					sender.sendMessage(failedTranslate("commands.disable.failed"));
				else {
					plugin.setEnable(false);
					plugin.taskStop();
					sender.sendMessage(successedTranslate("commands.disable.success"));
				}
			}
		});
		registerSubCommand(new SubCommand(this, subCommands.get("indent"), "command.showinfo.indent", translate("commands.indent.usage"), 1) {
			public void execute(CommandSender sender, String[] args) {
				if (!isNumber(args[0]))
					sender.sendMessage(failedTranslate("commands.generic.invalidNumber", args[0]));
				else {
					int indent = toInt(args[0]);
					plugin.setIndent(indent);
					sender.sendMessage(successedTranslate("commands.indent.success", valueOf(indent)));
				}
			}
		});
		registerSubCommand(new SubCommand(this, subCommands.get("period"), "command.showinfo.period", translate("commands.period.usage"), 1) {
			public void execute(CommandSender sender, String[] args) {
				if (!isNumber(args[0]))
					sender.sendMessage(failedTranslate("commands.generic.invalidNumber", args[0]));
				else {
					int period = toInt(args[0]);
					plugin.setPeriod(period);
					if (plugin.getEnable())
						plugin.taskStart();
					sender.sendMessage(successedTranslate("commands.period.success", valueOf(period)));
				}
			}
		});
		registerSubCommand(new SubCommand(this, subCommands.get("reload"), "command.showinfo.reload") {
			public void execute(CommandSender sender, String[] args) {
				plugin.saveDefaultData(false);
				plugin.loadAll();
				sender.sendMessage(successedTranslate("commands.reload.success"));
			}
		});
		registerSubCommand(new SubCommand(this, subCommands.get("save"), "command.showinfo.save") {
			public void execute(CommandSender sender, String[] args) {
				plugin.saveAll();
				sender.sendMessage(successedTranslate("commands.save.success"));
			}
		});
		registerSubCommand(new SubCommand(this, subCommands.get("reset"), "command.showinfo.reset") {
			public void execute(CommandSender sender, String[] args) {
				plugin.saveDefaultData(true);
				plugin.loadAll();
				if (plugin.getEnable())
					plugin.taskStart();
				sender.sendMessage(successedTranslate("commands.reset.success"));
			}
		});
	}

	public void registerSubCommand(SubCommand subCommand) {
		subCommands.put(subCommand.getName().toLowerCase(), subCommand);
	}

	public SubCommand getSubCommand(String command) {
		for (SubCommand subCommand : subCommands.values())
			if (subCommand.getName().equalsIgnoreCase(command))
				return subCommand;
		for (SubCommand subCommand : subCommands.values())
			if (subCommand.getData().equals(command))
				return subCommand;
		return null;
	}

	public ArrayList<SubCommand> getSubCommands() {
		return new ArrayList<SubCommand>(subCommands.values());
	}
}
