package io.github.jeremgamer.maintenancemanager.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import io.github.jeremgamer.maintenancemanager.MaintenanceManager;

public class JoinEvent implements Listener {

	public JoinEvent(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin (final PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (MaintenanceManager.getHandler().isOn()) {
			if(!player.hasPermission("maintenance.access")) {
					player.kickPlayer(MaintenanceManager.getInstance().getCustomConfig().getColoredString("maintenanceMessage"));
					return;
			} else
				player.sendMessage( MaintenanceManager.getInstance().getCustomConfig().getColoredString("loginMessage"));
		}
	}

}
