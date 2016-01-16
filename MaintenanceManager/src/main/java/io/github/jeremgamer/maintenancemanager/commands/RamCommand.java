package io.github.jeremgamer.maintenancemanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.hyperic.sigar.SigarException;

import io.github.jeremgamer.maintenancemanager.MaintenanceManager;
import io.github.jeremgamer.maintenancemanager.MaintenanceUtils;

public class RamCommand implements CommandExecutor {

	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 1)
			return false;
		if(sender.hasPermission("maintenance.ram"))
			Bukkit.getScheduler().runTaskAsynchronously(MaintenanceManager.getInstance(), new Runnable() {

				@Override
				public void run() {
					try {
						sender.sendMessage( MaintenanceManager.getInstance().getConfig().getString("ramUsage").replaceAll("&", "§").replaceAll("<ram%>", String.valueOf(MaintenanceUtils.getMemUsagePercent())).replaceAll("<ram>", String.valueOf(MaintenanceUtils.getMemUsage())));
					} catch (SigarException e) {
						e.printStackTrace();
					}
				}

			});
		return true;
	}

}
