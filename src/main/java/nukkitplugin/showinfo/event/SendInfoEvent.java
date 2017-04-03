package nukkitplugin.showinfo.event;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

public class SendInfoEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList	handlers	= new HandlerList();
	protected String					info		= "";
	private boolean						replace;

	public static HandlerList getHandlers() {
		return SendInfoEvent.handlers;
	}

	public SendInfoEvent(Player player, String info) {
		this(player, info, true);
	}

	public SendInfoEvent(Player player, String info, boolean replace) {
		this.player = player;
		this.info = info;
		this.setReplace(replace);
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public boolean isReplace() {
		return this.replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}
}
