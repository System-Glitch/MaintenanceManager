![alt tag](http://image.noelshack.com/fichiers/2014/19/1399313186-maintenance.png)

Maintenance Manager is the ultimate maintenance plugin which makes maintenances times on your server way easier! For the moment, it provides several exclusivities I havn't found anywhere else. It is very simple to use and to configure (even if the configuration is optionnal). I want to make it the best maintenance plugin for Bukkit!

Current version: 2.0

#Features:

  * /maintenance command manages the maintenance time: /maintenance on|off
  * Maintenance schedule. Schedule your maintenance time and advert players with /maintenance on (time before maintenance time) (Optionnal, in minutes)
  * Schedule interruption with /maintenance cancel.
  * Define the duration of your maintenance with /maintenance on [time before maintenance time] <duration> (Optionnal, in minutes)
  * Kicks non-op players and players who don't have the permission to log-in during the maintenance.
  * Prevents non authorized players to log-in during maintenance.
  * Changes the motd on the multi-player screen when the server is in maintenance.
  *  Commands can be casted from console or in-game.
  * Custom server icon for maintenance.
  * Server reload support.
  * You can reload the config without restarting the server or reloading it entirely with /maintenance reload.
  * Full permissions supported.
  * Full texts customization.
  * You can choose the number of slots available during a maintenance.
  * Manage your plugins in real-time without having to reload or restart the server with /maintenance (enable|disable) (plugin name). Disabled plugins stay disabled after restart or reload until you re-enable it.
  * Countdown in motd when a maintenance has a duration planned.
  * Aliases on /maintenance command. See the Commands section for further informations.
  * Backups on command. 

#Commands:

/maintenance is the main command. You'll have to add the following arguments:

Aliases: /mmode , /maint

  * on: turn on the maintenance mode
    * <schedule in minutes> (optionnal) Note: The schedule would cancel if you reload the server before the maintenance begin.
      * <duration of the maintenance in minutes> (optionnal) 
  * cancel: cancels a scheduled maintenance.
  * off: turn off the maintenance mode
  * reload: reload the plugin's config file
  * enable <plugin name>: enable the selected plugin.
  * disable <plugin name>: disable the selected plugin. Note: If you are not sure about the plugin name, type /plugins.
  * backup: Saves the server folder into a zip file located in the "backups" folder. 

#Permissions:

maintenance.*: Give acces to all MaintenanceManager commands.

maintenance.maintenance: Allows to start or stop a maintenance.

maintenance.maintenance.cancel: Allows to cancel a scheduled maintenance.

maintenance.backup: Allows to use the /maintenance backup command.

maintenance.access: Allows to enter the server during maintenance mode (and don't be kicked when the maintenance begin)

maintenance.reload: Allows to reload the config file.

maintenance.manage.plugins: Allows to disable or enable plugins.

Of course, OP players have these permissions.
Configuration:

By default, the config file looks like this:

```
#---------- MAINTENANCEMANAGER CONFIGURATION ----------#
maintenanceModeOnStart: false
remainingSeconds: 0
maxPlayersOnMaintenance: 10
disabledPlugins: []

#----------------------# 
#General messages      #
#----------------------#
maintenanceStart: '&2&o&lMaintenance time!'

maintenanceEnd: '&6&o&lMaintenance finished!'

maintenanceMessage: '&c&nThe server is currently under maintenance. Come back later.'

kickMessage: '&c&nThe server is currently under maintenance. Come back later.'

loginMessage: '&6&o&lThe server is currently in maintenance mode!'

cpuUsage: '&3CPU usage : &2&l<cpu>%'

ramUsage: '&3RAM usage : &2&l<ram%>% || <ram> MB'

#----------------------------------------------------# 
#----------------------------------------------------# 


#----------------------# 
#MOTD related          #
#----------------------#
maintenanceMOTD: '&6&o&lMaintenance mode...'

maintenanceWithDurationMOTD: '&6&o&lMaintenance mode...<n>&2&l<minutes> &r&eminutes
  remaining!'

maintenanceWithDurationMOTDLessThanOneMinute: '&6&o&lMaintenance mode...<n>&2Less
  than one minute remaining!'


#----------------------------------------------------# 
#----------------------------------------------------# 


#----------------------# 
#Schedule related      #
#----------------------#
scheduleMessage: '&5&oMaintenance in &4&l<minutes> &5&ominutes!'

scheduleCanceled: '&a&oThe scheduled maintenance have been canceled!'

scheduleLessThanOneMinute: '&5&oMaintenance in less than one minute!'


#----------------------------------------------------# 
#----------------------------------------------------# 


#----------------------# 
#Plugins management    #
#----------------------#
pluginDisabled: '&6&l<plugin> &a&osuccessfully disabled!'

pluginEnabled: '&6&l<plugin> &a&osuccessfully enabled!'


#----------------------------------------------------# 
#----------------------------------------------------# 


#----------------------# 
#Backup related        #
#----------------------#
backingUpMessage: '&e&oBacking up. The server will lag brievely.'

backupSuccess: '&2&o&lBackup success!'


#----------------------------------------------------# 
#----------------------------------------------------# 


#----------------------# 
#"Already" messages    #
#----------------------#
maintenanceAlreadyLaunched: '&cThe server is already in maintenance mode.'

noMaintenanceLaunched: '&cMaintenance mode is already off.'

noMaintenanceScheduled: '&cNo maintenance scheduled...'

maintenanceAlreadyScheduled: '&cA maintenance is already scheduled.'

alreadyBackingUp: '&cPlease wait the end of this backup before backing up again!'


#----------------------------------------------------# 
#----------------------------------------------------# 


#----------------------# 
#Error messages        #
#----------------------#
inputErrorSchedule: '&cThe time value for schedule must be an integer!'

inputErrorDuration: '&cThe time value for duration must be an integer!'

pluginManagementArgumentErrorDisable: '&cPlease name the plugin you want to disable!'

pluginManagementArgumentErrorEnable: '&cPlease name the plugin you want to enable!'


#----------------------------------------------------# 
#----------------------------------------------------#
```

To set your custom icon for maintenances, just put an image named maintenance-icon.png in the main folder of your server. The default one auto-creates if the plugins doesn't find any maintenance-icon.png in that folder. Note: Your icon must be a 64X64 png image!

Advice: If you don't want to change the icon, copy-paste server-icon.png and rename it into maintenance-icon.png

Important: If your server is running without icon, maintenance-icon.png will be displayed even out of a maintenance!

To add colors to your texts, just put '§' and a character from 0-9 and a-f before the words you want to color. Please refer you to this page to know formatting codes.
Setup:

To set up Maintenance Manager, simply download the jar file and put it into "plugins" folder. And now enjoy the uniqueness of MaintenanceManager!

#Feedback:

Your opinion is important for me! Please write a comment below and let me know what you think, what you would like to see in the plugin or what you would like to change! Please report me any bug you have. :) I want to make the best maintenance plugin for Bukkit, and to do that, I need your feedback! Thank you for your time.
Donate!

If you really enjoy this plugin and you want to reward me for my work, please donate! I would be very grateful!

<html><a href="https://www.paypal.com/cgi-bin/webscr?cmd=_donations&amp;business=99FBSV9DP5JCL&amp;lc=US&amp;item_name=JEREMSPEED&amp;item_number=MaintenanceManager&amp;currency_code=USD&amp;bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted"><img src="https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif" alt="Donate" title="Donate"></a></html>
