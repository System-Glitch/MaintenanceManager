package io.github.jeremgamer.maintenancemanager.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;

import io.github.jeremgamer.maintenancemanager.MaintenanceManager;

public class JoinEvent implements Listener {
	
	public JoinEvent(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin (final PlayerLoginEvent event) {
    	Player player = event.getPlayer();
    	if (MaintenanceManager.getHandler().isOn() && !player.hasPermission("maintenance.acess")) {    		
    		event.disallow( org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER, MaintenanceManager.getInstance().getConfig().getString("maintenanceMessage").replaceAll("&", "§") );
    		return;
    	} else if (MaintenanceManager.getHandler().isOn() && player.hasPermission( "maintenance.access" )) {
    		event.allow();
    	}
    	
    }
    
    @EventHandler
    public void onJoin (final PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	if (MaintenanceManager.getHandler().isOn()) 
    		player.sendMessage( MaintenanceManager.getInstance().getConfig().getString("loginMessage").replaceAll("&", "§") );
    	
    	if (MaintenanceManager.isUpToDate()) {
    		player.sendMessage("§c§lYour MaintenanceManager is outdated! \n§6§oGet the latest version here: §e§n" + MaintenanceManager.DOWNLOAD_ADDRESS);
    	}    	
    }

}
