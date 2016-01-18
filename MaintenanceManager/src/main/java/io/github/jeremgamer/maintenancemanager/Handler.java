package io.github.jeremgamer.maintenancemanager;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Handler {

	private boolean maintenanceTime;
	private boolean scheduleEnabled;
	private boolean durationEnabled;

	private int scheduleTask;
	private int durationTask;

	private int duration;
	private int scheduleTime;

	Handler() {
		scheduleTask = -1;
		durationTask = -1;
		duration = -1;
		scheduleTime = -1;
		initSavedData();
	}

	public void initSavedData() {
		File configFile = new File(MaintenanceManager.getInstance().getDataFolder(), "config.yml");
		if (!configFile.exists())
			MaintenanceManager.getInstance().saveDefaultConfig();
		MaintenanceManager.getInstance().getConfig().options().header("---------- MAINTENANCEMANAGER CONFIGURATION ----------#").copyHeader(true);
		duration = MaintenanceManager.getInstance().getConfig().getInt("remainingSeconds");
		if (duration != 0) {
			maintenanceWithDuration(duration/60);
		} else
			maintenanceTime = MaintenanceManager.getInstance().getConfig().getBoolean("maintenanceModeOnStart");

		List<String> disabledPlugins = MaintenanceManager.getInstance().getConfig().getStringList("disabledPlugins");
		if ( !disabledPlugins.isEmpty() )
			MaintenanceUtils.disablePlugin(disabledPlugins.toArray(new String[disabledPlugins.size()]));
	}

	public boolean isOn(){
		return maintenanceTime;
	}

	public boolean isScheduleEnabled() {
		return scheduleEnabled;
	}

	public boolean isDurationEnabled() {
		return durationEnabled;
	}

	public int getRemainingTime() {
		return duration;
	}

	private void decrementDuration() {
		duration--;
	}

	private void decrementSchedule() {
		scheduleTime--;
	}

	public int getScheduleTime() {
		return scheduleTime;
	}

	private void setScheduleTime(int time) {
		scheduleTime = time;
	}

	public void normalMaintenance(CommandSender sender) {
		if(!maintenanceTime) {
			if (!scheduleEnabled) {
				maintenanceTime = true;
				MaintenanceManager.getInstance().getConfig().set("maintenanceModeOnStart", true);	 
				MaintenanceManager.getInstance().saveConfig();
				Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("maintenanceStart").replaceAll("&", "§") );
				Bukkit.getScheduler().runTask(MaintenanceManager.getInstance(), new Runnable() {
					@Override
					public void run() {
						for (Player player: Bukkit.getServer().getOnlinePlayers())
							if ( !player.hasPermission( "maintenance.access" ) && !player.isOp() ) 
								player.kickPlayer( MaintenanceManager.getInstance().getConfig().getString("kickMessage").replaceAll("&", "§") );
					}

				});
			} else
				sender.sendMessage( MaintenanceManager.getInstance().getConfig().getString("maintenanceAlreadyScheduled").replaceAll("&", "§") );
		} else
			sender.sendMessage( MaintenanceManager.getInstance().getConfig().getString("maintenanceAlreadyLaunched").replaceAll("&", "§") );
	}

	public void normalMaintenance() {
		if(!maintenanceTime)
			if (!scheduleEnabled) {
				maintenanceTime = true;
				MaintenanceManager.getInstance().getConfig().set("maintenanceModeOnStart", true);	 
				MaintenanceManager.getInstance().saveConfig();
				Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("maintenanceStart").replaceAll("&", "§") );
				Bukkit.getScheduler().runTask(MaintenanceManager.getInstance(), new Runnable() {
					@Override
					public void run() {
						for (Player player: Bukkit.getServer().getOnlinePlayers())
							if ( !player.hasPermission( "maintenance.access" ) && !player.isOp() ) 
								player.kickPlayer( MaintenanceManager.getInstance().getConfig().getString("kickMessage").replaceAll("&", "§") );
					}

				});
			}
	}

	@SuppressWarnings("deprecation")
	public void scheduledMaintenance(CommandSender sender , final int scheduleTime) {
		if(!maintenanceTime) {
			if (!scheduleEnabled) {
				if(scheduleTime <= 0) {
					normalMaintenance();
					return;
				}
				Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("scheduleMessage").replaceAll("&", "§").replaceAll("<minutes>", String.valueOf((int)(scheduleTime))) );
				setScheduleTime(scheduleTime*60);
				scheduleEnabled = true;
				scheduleTask = Bukkit.getScheduler().scheduleAsyncRepeatingTask(MaintenanceManager.getInstance(), new Runnable() {

					@Override
					public void run() {
						decrementSchedule();

						if(getScheduleTime() <= 0) {
							scheduleEnabled = false;
							Bukkit.getScheduler().cancelTask(scheduleTask);
							normalMaintenance();
						} else if(getScheduleTime() == 60)
							Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("scheduleLessThanOneMinute").replaceAll("&", "§") );
						else if(((getScheduleTime() == 60*2 && scheduleTime > 2) ||
								(getScheduleTime() == 60*3 && scheduleTime > 3) ||
								(getScheduleTime() == 60*4 && scheduleTime > 4) ||
								(getScheduleTime() == 60*5 && scheduleTime > 5)))
							Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("scheduleMessage").replaceAll("&", "§").replaceAll("<minutes>", String.valueOf((int)(getScheduleTime()/60))) );
						else if(getScheduleTime() == (scheduleTime*60 / 2) && getScheduleTime() > 60)
							Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("scheduleMessage").replaceAll("&", "§").replaceAll("<minutes>", String.valueOf((int)(getScheduleTime()/60))) );
					}

				}, 0L , 20L);
			} else
				sender.sendMessage( MaintenanceManager.getInstance().getConfig().getString("maintenanceAlreadyScheduled").replaceAll("&", "§") );
		} else
			sender.sendMessage( MaintenanceManager.getInstance().getConfig().getString("maintenanceAlreadyLaunched").replaceAll("&", "§") );
	}

	@SuppressWarnings("deprecation")
	public void scheduledMaintenance(CommandSender sender , final int scheduleTime , final int duration) {
		if(!maintenanceTime) {
			if (!scheduleEnabled) {
				if(scheduleTime <= 0) {
					maintenanceWithDuration(duration);
					return;
				}
				Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("scheduleMessage").replaceAll("&", "§").replaceAll("<minutes>", String.valueOf((int)(scheduleTime))) );
				setScheduleTime(scheduleTime*60);
				scheduleEnabled = true;
				scheduleTask = Bukkit.getScheduler().scheduleAsyncRepeatingTask(MaintenanceManager.getInstance(), new Runnable() {

					@Override
					public void run() {
						decrementSchedule();

						if(getScheduleTime() <= 0) {
							scheduleEnabled = false;
							Bukkit.getScheduler().cancelTask(scheduleTask);
							maintenanceWithDuration(duration);
						} else if(getScheduleTime() == 60)
							Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("scheduleLessThanOneMinute").replaceAll("&", "§") );
						else if(((getScheduleTime() == 60*2 && scheduleTime > 2) ||
								(getScheduleTime() == 60*3 && scheduleTime > 3) ||
								(getScheduleTime() == 60*4 && scheduleTime > 4) ||
								(getScheduleTime() == 60*5 && scheduleTime > 5)))
							Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("scheduleMessage").replaceAll("&", "§").replaceAll("<minutes>", String.valueOf((int)(getScheduleTime()/60))) );
						else if(getScheduleTime() == (scheduleTime*60 / 2) && getScheduleTime() > 60)
							Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("scheduleMessage").replaceAll("&", "§").replaceAll("<minutes>", String.valueOf((int)(getScheduleTime()/60))) );
					}

				}, 0L , 20L);
			} else
				sender.sendMessage( MaintenanceManager.getInstance().getConfig().getString("maintenanceAlreadyScheduled").replaceAll("&", "§") );
		} else
			sender.sendMessage( MaintenanceManager.getInstance().getConfig().getString("maintenanceAlreadyLaunched").replaceAll("&", "§") );
	}

	@SuppressWarnings("deprecation")
	public void maintenanceWithDuration(double duration) {
		if(maintenanceTime)
			return;
		if (!scheduleEnabled) {
			this.duration = (int) (duration*60);
			normalMaintenance();
			durationEnabled = true;
			durationTask = Bukkit.getScheduler().scheduleAsyncRepeatingTask(MaintenanceManager.getInstance(), new Runnable() {

				@Override
				public void run() {
					decrementDuration();
					MaintenanceManager.getInstance().getConfig().set( "remainingSeconds" , getRemainingTime());                	 
					MaintenanceManager.getInstance().saveConfig();
					if(getRemainingTime() <= 0)
						stopMaintenance();
				}

			}, 0L, 20L);
		}
	}

	public void stopMaintenance(CommandSender sender) {
		if(maintenanceTime) {
			if(durationEnabled) {
				Bukkit.getScheduler().cancelTask(durationTask);
				durationEnabled = false;
				duration = -1;
			}
			maintenanceTime = false;
			MaintenanceManager.getInstance().getConfig().set("maintenanceModeOnStart", false);
			Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("maintenanceEnd").replaceAll("&", "§") );                	 
			MaintenanceManager.getInstance().saveConfig();			
		} else
			sender.sendMessage(MaintenanceManager.getInstance().getConfig().getString("noMaintenanceLaunched").replaceAll("&", "§"));
	}

	public void stopMaintenance() {
		if(maintenanceTime) {
			if(durationEnabled) {
				Bukkit.getScheduler().cancelTask(durationTask);
				durationEnabled = false;
				duration = -1;
			}
			maintenanceTime = false;
			MaintenanceManager.getInstance().getConfig().set("maintenanceModeOnStart", false);
			Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("maintenanceEnd").replaceAll("&", "§") );                	 
			MaintenanceManager.getInstance().saveConfig();			
		}
	}

	public void cancelSchedule() {
		if(scheduleEnabled) {
			Bukkit.getScheduler().cancelTask(scheduleTask);
			scheduleEnabled = false;
			scheduleTime = -1;
			Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("scheduleCanceled").replaceAll("&", "§") );
		}
	}

	public void cancelSchedule(CommandSender sender) {
		if(scheduleEnabled) {
			Bukkit.getScheduler().cancelTask(scheduleTask);
			scheduleEnabled = false;
			scheduleTime = -1;
			Bukkit.getServer().broadcastMessage( MaintenanceManager.getInstance().getConfig().getString("scheduleCanceled").replaceAll("&", "§") );
		} else
			sender.sendMessage(MaintenanceManager.getInstance().getConfig().getString("noMaintenanceScheduled").replaceAll("&", "§"));
	}

}
