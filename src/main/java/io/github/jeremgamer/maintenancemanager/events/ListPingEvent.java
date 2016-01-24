package io.github.jeremgamer.maintenancemanager.events;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.CachedServerIcon;

import io.github.jeremgamer.maintenancemanager.MaintenanceManager;

public class ListPingEvent implements Listener {

	private CachedServerIcon maintenanceIcon;

	public ListPingEvent(Plugin plugin) {
		loadIcon();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void loadIcon() {
		File srcIcon = new File("maintenance-icon.png");
		if (!srcIcon.exists()) {
			try (InputStream input = MaintenanceManager.class.getResourceAsStream("/maintenance-icon.png");
					OutputStream output = new FileOutputStream(srcIcon)) {
				byte[] buf = new byte[8192];
				int len;
				while ( (len=input.read(buf)) > 0 ) {
					output.write(buf, 0, len);
				}
				output.flush();
				input.close();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedImage maintenanceImage;
		try {
			maintenanceImage = ImageIO.read(srcIcon);
			maintenanceIcon = Bukkit.getServer().loadServerIcon(maintenanceImage);
		} catch (Exception e) {
			MaintenanceManager.getInstance().getLogger().severe(e.getMessage());
		}
	}

	public CachedServerIcon getIcon() {
		return maintenanceIcon;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void MaintenanceListPing (final ServerListPingEvent event) {
		if (MaintenanceManager.getHandler().isOn()) {
			if (MaintenanceManager.getHandler().isDurationEnabled()) {
				try {
					if (MaintenanceManager.getHandler().getRemainingTime() / 60 < 1) {
						event.setMotd( MaintenanceManager.getInstance().getCustomConfig().getString("maintenanceWithDurationMOTDLessThanOneMinute").replaceAll("&", "�"));
					} else {
						event.setMotd( MaintenanceManager.getInstance().getCustomConfig().getString("maintenanceWithDurationMOTD").replaceAll("&", "�").replaceAll("<minutes>", String.valueOf((int)(MaintenanceManager.getHandler().getRemainingTime() / 60))));
					}
				} catch (NumberFormatException e){
					MaintenanceManager.getInstance().getLogger().info(MaintenanceManager.getInstance().getCustomConfig().getString("inputErrorDuration").replaceAll("&", "�"));
				}
			} else {
				event.setMotd( MaintenanceManager.getInstance().getCustomConfig().getString("maintenanceMOTD").replaceAll("&", "�") );
			}
			try {
				event.setServerIcon(maintenanceIcon); 
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			event.setMaxPlayers( MaintenanceManager.getInstance().getCustomConfig().getInt("maxPlayersOnMaintenance") );
		}  	
	}

}
