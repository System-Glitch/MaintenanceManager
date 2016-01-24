package io.github.jeremgamer.maintenancemanager;

import java.io.*;
import java.net.URL;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.jeremgamer.maintenancemanager.commands.CpuCommand;
import io.github.jeremgamer.maintenancemanager.commands.MaintenanceCommand;
import io.github.jeremgamer.maintenancemanager.commands.RamCommand;
import io.github.jeremgamer.maintenancemanager.events.JoinEvent;
import io.github.jeremgamer.maintenancemanager.events.ListPingEvent;

public class MaintenanceManager extends JavaPlugin {

	//public static final String DOWNLOAD_ADDRESS = "https://raw.githubusercontent.com/JeremGamer/MaintenanceManager/master/release/MaintenanceManager-<version>.jar";
	//private static final String RELEASE_MANIFEST = "https://raw.githubusercontent.com/JeremGamer/MaintenanceManager/master/RELEASE_MANIFEST.version";
	public static final String DOWNLOAD_ADDRESS = "file:/home/david/Desktop/test/MaintenanceManager-<version>.jar";
	private static final String RELEASE_MANIFEST = "file:/home/david/Desktop/test/RELEASE_MANIFEST.version";
	private static MaintenanceManager instance;
	private static Handler handler;
	private static boolean upToDate;

	private ListPingEvent listPing;
	private MaintenanceCommand command;

	private SaveFile config;
	private File configFile;

	private boolean checkUpdate () {
		try {
			URL u = new URL(RELEASE_MANIFEST);

			InputStream is;
			is = u.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String s = reader.readLine();

			if(s == null)return true;
			final String version = s.replace(":VERSION:" , "");
			if (!version.equals(getDescription().getVersion())) {
				File old = new File(getDataFolder() , "MaintenanceManager-"+getDescription().getVersion()+".jar");
				old.createNewFile();
				FileUtils.copy(new FileInputStream(getFile()) , new FileOutputStream(old));
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							FileUtils.copy(new URL(DOWNLOAD_ADDRESS.replace("<version>" , version)).openStream() , new FileOutputStream("plugins" + File.separator +"MaintenanceMananger-" + version + ".jar"));
							getFile().delete();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} , "MaintenanceMananger"));
				getLogger().info("MaintenanceManager isn't up to date! New version downloaded v :" + version + " reboot server to apply update");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
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

	public void onEnable() {
		instance = this;
		initConfig();
		handler = new Handler();
		upToDate = checkUpdate();
		initEvents();
		registerCommands();
	}

	private void registerCommands() {
		getLogger().info("Registering commands...");
		command = new MaintenanceCommand();
		this.getCommand("maintenance").setExecutor(command);
		this.getCommand("cpu").setExecutor(new CpuCommand());
		this.getCommand("ram").setExecutor(new RamCommand());
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
