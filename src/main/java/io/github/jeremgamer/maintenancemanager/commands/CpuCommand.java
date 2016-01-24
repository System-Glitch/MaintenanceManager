package io.github.jeremgamer.maintenancemanager.commands;

import io.github.jeremgamer.maintenancemanager.MaintenanceUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.github.jeremgamer.maintenancemanager.MaintenanceManager;


public class CpuCommand implements CommandExecutor {

	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
		if(args.length != 0)
			return false;
		if(sender.hasPermission("maintenance.cpu"))
			sender.sendMessage( MaintenanceManager.getInstance().getCustomConfig().getColoredString("cpuUsage").replaceAll("<cpu>", Long.toString(Math.round(MaintenanceUtils.getOsMBeam().getSystemCpuLoad()*100))));
		return true;
	}

}
