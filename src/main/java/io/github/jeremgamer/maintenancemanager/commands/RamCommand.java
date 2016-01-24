package io.github.jeremgamer.maintenancemanager.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.github.jeremgamer.maintenancemanager.MaintenanceManager;

public class RamCommand implements CommandExecutor {

	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
		if(args.length != 0 || !sender.hasPermission("maintenance.ram"))return false;
			long usedRam = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			sender.sendMessage( MaintenanceManager.getInstance().getCustomConfig().getColoredString("ramUsage").replace("<ram%>", Long.toString(Math.round((double)usedRam/(double)Runtime.getRuntime().totalMemory()*100)).replace("<ram>", Long.toString(usedRam/1048576))));
		return true;
	}
}
