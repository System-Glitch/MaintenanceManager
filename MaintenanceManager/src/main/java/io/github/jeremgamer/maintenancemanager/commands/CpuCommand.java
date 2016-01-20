package io.github.jeremgamer.maintenancemanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.hyperic.sigar.SigarException;

import io.github.jeremgamer.maintenancemanager.MaintenanceManager;
import io.github.jeremgamer.maintenancemanager.MaintenanceUtils;

public class CpuCommand implements CommandExecutor {

	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 1)
			return false;
		if(sender.hasPermission("maintenance.cpu"))
			Bukkit.getScheduler().runTaskAsynchronously(MaintenanceManager.getInstance(), new Runnable() {

				@Override
				public void run() {
					try {
						sender.sendMessage( MaintenanceManager.getInstance().getCustomConfig().getString("cpuUsage").replaceAll("&", "§").replaceAll("<cpu>", String.valueOf(MaintenanceUtils.getCpuUsage())));
					} catch (SigarException e) {
						e.printStackTrace();
					}
				}
				
			});
		return true;
	}

}
