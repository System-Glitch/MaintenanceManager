package io.github.jeremgamer.maintenancemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sun.management.OperatingSystemMXBean;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public abstract class MaintenanceUtils {

	private static boolean backingUp = false;
	private static ArrayList<String> disabledPlugins = new ArrayList<String>();


	private static OperatingSystemMXBean osMBeam;
	static {
		try {
			osMBeam = ManagementFactory.newPlatformMXBeanProxy(ManagementFactory.getPlatformMBeanServer(), ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Plugin getPluginIgnoreCase(final String pluginName) {
		for(final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if(plugin.getName().equalsIgnoreCase(pluginName)) {
				return plugin;
			}
		}
		return null;
	}

	public static void disablePlugin(final String... toDisable) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(MaintenanceManager.getInstance(), new Runnable() {

			@Override
			public void run() {
				MaintenanceManager.getInstance().getLogger().info("Disabling plugins...");
				PluginManager pm = Bukkit.getPluginManager();
				for ( String name : toDisable ) {
					try {
						Plugin plugin = pm.getPlugin(name);
						pm.disablePlugin(plugin);
						disabledPlugins.add(plugin.getName());
					} catch (NullPointerException e1) {
						MaintenanceManager.getInstance().getLogger().severe( "No plugin named " + name + " to disable!" );
					}
				}
			}

		}, 1L);
	}

	public static void enablePlugin(CommandSender sender , final String toEnable) {
		try {
			Plugin plugin = getPluginIgnoreCase(toEnable);
			Bukkit.getPluginManager().enablePlugin(plugin);
			disabledPlugins.remove(plugin.getName());
			MaintenanceManager.getInstance().getCustomConfig().set("disabledPlugins", disabledPlugins);            	 
			MaintenanceManager.getInstance().saveConfig();
			sender.sendMessage( MaintenanceManager.getInstance().getCustomConfig().getString("pluginEnabled").replaceAll("&", "�").replaceAll("<plugin>", plugin.getName()) );
		} catch (NullPointerException e1) {
			sender.sendMessage(MaintenanceManager.getInstance().getCustomConfig().getString("pluginManagementArgumentErrorEnable").replaceAll("&", "�"));
		}
	}

	public static void disablePlugin(CommandSender sender , final String toDisable) {
		try {
			Plugin plugin = getPluginIgnoreCase(toDisable);
			Bukkit.getPluginManager().disablePlugin(plugin);
			disabledPlugins.add(plugin.getName());
			MaintenanceManager.getInstance().getCustomConfig().set("disabledPlugins", disabledPlugins);            	 
			MaintenanceManager.getInstance().saveConfig();
			sender.sendMessage( MaintenanceManager.getInstance().getCustomConfig().getColoredString("pluginDisabled").replace("<plugin>", plugin.getName()) );
		} catch (NullPointerException e1) {
			sender.sendMessage(MaintenanceManager.getInstance().getCustomConfig().getColoredString("pluginManagementArgumentErrorDisable"));
		}
	}

	public static void backup(final CommandSender sender) {
		if (!backingUp) {
			backingUp = true;
			String folderPath = new File("").getAbsolutePath() + "/backups/";
			File folder = new File(folderPath);
			if (!folder.exists()) {
				if (folder.mkdir())
					MaintenanceManager.getInstance().getLogger().info("Backup folder created!");
				else
					MaintenanceManager.getInstance().getLogger().warning("Failed to create backup folder...");
			}

			MaintenanceManager.getInstance().getServer().broadcastMessage( MaintenanceManager.getInstance().getCustomConfig().getString("backingUpMessage").replaceAll("&", "�") );
			MaintenanceManager.getInstance().getLogger().info("Backing up...");


			MaintenanceManager.getInstance().getServer().dispatchCommand(MaintenanceManager.getInstance().getServer().getConsoleSender(), "save-all");
			MaintenanceManager.getInstance().getServer().dispatchCommand(MaintenanceManager.getInstance().getServer().getConsoleSender(), "save-off");

			Bukkit.getScheduler().runTaskAsynchronously(MaintenanceManager.getInstance(), new Runnable() {

				@Override
				public void run() {
					backupProcess();
					if(sender != null)
						sender.sendMessage( MaintenanceManager.getInstance().getCustomConfig().getString("backupSuccess").replaceAll("&", "�") );
					MaintenanceManager.getInstance().getServer().dispatchCommand(MaintenanceManager.getInstance().getServer().getConsoleSender(), "save-on");
					backingUp = false;
				}
				
			});

		} else {
			if(sender != null)
				sender.sendMessage( MaintenanceManager.getInstance().getCustomConfig().getString("alreadyBackingUp").replaceAll("&", "�") );
		}
	}

	private static void backupProcess()  {

		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd - HH mm ss");
		String zipFile = "backups/" + dateFormat.format(cal.getTime()) + ".zip";
		FileUtils.zip(new File(zipFile).getAbsoluteFile() , new File(System.getProperty("user.dir")) , new File("backups"));
		MaintenanceManager.getInstance().getLogger().info("Backup success!");
	}



	public static OperatingSystemMXBean getOsMBeam() {
		return osMBeam;
	}

}
