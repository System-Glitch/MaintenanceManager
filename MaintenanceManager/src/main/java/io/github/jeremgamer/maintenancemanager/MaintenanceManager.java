package io.github.jeremgamer.maintenancemanager;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.jeremgamer.maintenancemanager.commands.CpuCommand;
import io.github.jeremgamer.maintenancemanager.commands.MaintenanceCommand;
import io.github.jeremgamer.maintenancemanager.commands.RamCommand;
import io.github.jeremgamer.maintenancemanager.events.JoinEvent;
import io.github.jeremgamer.maintenancemanager.events.ListPingEvent;

public class MaintenanceManager extends JavaPlugin {

	public static final String VERSION = "2.1";
	public static final String DOWNLOAD_ADDRESS = "goo.gl/8Tojtm";
	private static final String RELEASE_MANIFEST = "https://github.com/JeremGamer/MaintenanceManager/blob/master/RELEASE_MANIFEST.version";
	private static MaintenanceManager instance;
	private static Handler handler;
	private static boolean upToDate;
	private static boolean libsLoaded;

	private ListPingEvent listPing;
	private MaintenanceCommand command;

	private SaveFile config;
	private File configFile;

	@SuppressWarnings("deprecation")
	private boolean checkUpdate () {		
		if(!checkInternet())
			return true;
		try {

			String version;
			String s;

			URL u = new URL(RELEASE_MANIFEST);

			InputStream is;
			try {
				is = u.openStream();
			} catch (FileNotFoundException fnfe) {
				return true;
			}
			DataInputStream dis = new DataInputStream(new BufferedInputStream(is));

			while ((s = dis.readLine()) != null) {
				if (s.contains("VERSION:")) {
					version = s.substring(s.indexOf(":VERSION:") , s.lastIndexOf(":VERSION:"));
					version = version.substring(":VERSION:".length());
					if (!version.equals(VERSION)) {
						getLogger().info("MaintenanceManager isn't up to date! Get the " + version + " version here: " + DOWNLOAD_ADDRESS);
						return false;
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private static boolean checkInternet() {
		try {
			try {
				URL url = new URL("http://www.google.com");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.connect();
				if (con.getResponseCode() == 200){
					return true;
				}
			} catch (Exception exception) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}	

	public static MaintenanceManager getInstance() {
		return instance;
	}

	public static Handler getHandler() {
		return handler;
	}

	public static boolean isUpToDate() {
		return upToDate;
	}

	@SuppressWarnings("static-access")
	public void onEnable() {
		this.instance = this;
		initConfig();
		this.handler = new Handler();
		upToDate = checkUpdate();
		initEvents();
		try {
			libsLoaded = MaintenanceUtils.loadLibraries();
		} catch (IOException e) {
			e.printStackTrace();
		}
		registerCommands();
	}

	private void registerCommands() {
		getLogger().info("Registering commands...");
		command = new MaintenanceCommand();
		this.getCommand("maintenance").setExecutor(command);
		if(libsLoaded) {
			this.getCommand("cpu").setExecutor(new CpuCommand());
			this.getCommand("ram").setExecutor(new RamCommand());
		}
	}

	private void initEvents() {
		getLogger().info("Initializing events...");
		listPing = new ListPingEvent(this);
		new JoinEvent(this);
	}

	public void reload() {
		getLogger().info("Reloading...");
		reloadConfig();
		handler.initSavedData();
		listPing.loadIcon();
		command.loadHelp();
		upToDate = checkUpdate();
		for(Player p : Bukkit.getOnlinePlayers())
			if (!isUpToDate() && p.isOp())
				p.sendMessage("§c§lYour MaintenanceManager is outdated! \n§6§oGet the latest version here: §e§n" + DOWNLOAD_ADDRESS);
	}

	private void initConfig() {
		config = new SaveFile();
		configFile = new File(MaintenanceManager.getInstance().getDataFolder(), "config.yml");
		if (!configFile.exists())
			MaintenanceManager.getInstance().saveDefaultConfig();
		try {
			config.load(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		updateConfig();
	}
	
	private void updateConfig() {
		if(!config.containsSection("scheduleMessageSeconds")) {
			config.createSection(config.indexOf("scheduleMessage")+1 , "scheduleMessageSeconds", "&5&oMaintenance in &4&l<seconds> &5&oseconds!");
			try {
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void reloadConfig() {
		try {
			if (!configFile.exists())
				config.save(configFile);
			else
				config.load(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SaveFile getCustomConfig() {
		return config;
	}

	@Override
	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
