package nukkitplugin.showinfo.command.subcommand;

import static java.util.Arrays.*;
import static nukkitplugin.showinfo.utils.Translation.translate;

import cn.nukkit.command.CommandSender;
import cn.nukkit.permission.Permissible;
import nukkitplugin.showinfo.command.ShowInfoCommand;

public abstract class SubCommand {
	protected ShowInfoCommand	mainCommand;
	protected String			permission		= "";
	protected String			usage			= "";
	protected int				needArgCount	= 0;
	private SubCommandData		data;

	public SubCommand() {}

	public SubCommand(ShowInfoCommand mainCommand, SubCommandData data, String permission) {
		this.mainCommand = mainCommand;
		this.data = data;
		this.permission = permission;
	}

	public SubCommand(ShowInfoCommand mainCommand, SubCommandData data, String permission, String usage, int needArgCount) {
		this(mainCommand, data, permission);
		this.usage = usage;
		this.needArgCount = needArgCount;
	}

	public void run(CommandSender sender, String[] args) {
		if (args.length < getNeedArgCount() || stream(copyOfRange(args, 0, getNeedArgCount())).anyMatch(arg -> arg.equals("")))
			sender.sendMessage(translate("prefix") + " " + translate("commands.generic.usage", getUsage()));
		else
			execute(sender, args);
	}

	public abstract void execute(CommandSender sender, String[] args);

	public String getName() {
		return data.getCommand();
	}

	public SubCommandData getData() {
		return data;
	}

	public String getPermission() {
		return permission;
	}

	public String getUsage() {
		return translate("commands.generic.usages", new String[] { mainCommand.getLabel(), getName(), usage });
	}

	public int getNeedArgCount() {
		return needArgCount;
	}

	public boolean hasPermission(Permissible sender) {
		return sender.hasPermission(getPermission());
	}
}
