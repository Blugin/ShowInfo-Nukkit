package nukkitplugin.showinfo.command.subcommand;

import java.util.ArrayList;

public class SubCommandData {
	protected String			command;
	protected ArrayList<String>	aliases;

	public SubCommandData(String command, ArrayList<String> aliases) {
		this.command = command;
		this.aliases = aliases;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String name) {
		this.command = name;
	}

	public ArrayList<String> getAliases() {
		return aliases;
	}

	public void setAliases(ArrayList<String> aliases) {
		this.aliases = aliases;
	}

	public boolean equals(String string) {
		return command.equalsIgnoreCase(string) || aliases.contains(string.toLowerCase());
	}
}
