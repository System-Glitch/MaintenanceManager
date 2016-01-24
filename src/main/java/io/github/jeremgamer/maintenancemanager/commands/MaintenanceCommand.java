package io.github.jeremgamer.maintenancemanager.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.jeremgamer.maintenancemanager.MaintenanceManager;
import io.github.jeremgamer.maintenancemanager.MaintenanceUtils;

public class MaintenanceCommand implements CommandExecutor {

	private String help;

	public MaintenanceCommand() {
		loadHelp();
	}

	public void loadHelp() {
		InputStream input = MaintenanceManager.class.getResourceAsStream("/help.txt");
		InputStreamReader isr = new InputStreamReader(input);
		help = "";
		try {
			BufferedReader bw = new BufferedReader(isr);
			for(int i = 0; i < 14; i++) {
				help += ChatColor.translateAlternateColorCodes('&', bw.readLine() + "\n");
			}
		} catch (IOException e) {
			MaintenanceManager.getInstance().getLogger().warning(e.getMessage());
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0 || args.length > 3) {
			sender.sendMessage(help);
			return true;
		}
		if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("maintenance.reload")) {
			if(sender instanceof Player)
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "Reloading...");
			MaintenanceManager.getInstance().reload();
			sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.ITALIC.toString() + "MaintenanceManager config reloaded!");
			return true;
		} else if(args[0].equalsIgnoreCase("on") && sender.hasPermission("maintenance.maintenance")) {

			if(args.length == 1)
				MaintenanceManager.getHandler().normalMaintenance(sender);

			else if(args.length == 2) {
				try {
					int scheduleTime = Integer.parseInt(args[1]);
					MaintenanceManager.getHandler().scheduledMaintenance(sender, scheduleTime);
				} catch (NumberFormatException e) {
					sender.sendMessage(MaintenanceManager.getInstance().getCustomConfig().getColoredString("inputErrorSchedule"));
					return true;
				}

			} else if(args.length == 3) {
				int scheduleTime = 0;
				try {
					scheduleTime = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(MaintenanceManager.getInstance().getCustomConfig().getColoredString("inputErrorSchedule"));
					return true;
				}
				int duration;
				try {
					duration = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					sender.sendMessage(MaintenanceManager.getInstance().getCustomConfig().getColoredString("inputErrorDuration"));
					return true;
				}
				MaintenanceManager.getHandler().scheduledMaintenance(sender, scheduleTime, duration);
			}

			return true;

		} else if(args[0].equalsIgnoreCase("off") && sender.hasPermission("maintenance.maintenance") && args.length == 1) {
			MaintenanceManager.getHandler().stopMaintenance(sender);
			return true;
		} else if(args[0].equalsIgnoreCase("disable") && sender.hasPermission("maintenance.manage.plugins")) {
			if(args.length == 2)
				MaintenanceUtils.disablePlugin(sender, args[1]);
			else
				sender.sendMessage(MaintenanceManager.getInstance().getCustomConfig().getColoredString("pluginManagementArgumentErrorDisable"));
			return true;
		} else if(args[0].equalsIgnoreCase("enable") && sender.hasPermission("maintenance.manage.plugins")) {
			if(args.length == 2)
				MaintenanceUtils.enablePlugin(sender, args[1]);
			else
				sender.sendMessage(MaintenanceManager.getInstance().getCustomConfig().getColoredString("pluginManagementArgumentErrorEnable"));
			return true;
		} else if(args[0].equalsIgnoreCase("cancel") && sender.hasPermission("maintenance.maintenance.cancel") && args.length == 1) {
			MaintenanceManager.getHandler().cancelSchedule(sender);
			return true;
		} else if(args[0].equalsIgnoreCase("backup") && sender.hasPermission("maintenance.backup")) {
			MaintenanceUtils.backup(sender);
			return true;
		} else
			sender.sendMessage(help);
		return true;
	}

}
