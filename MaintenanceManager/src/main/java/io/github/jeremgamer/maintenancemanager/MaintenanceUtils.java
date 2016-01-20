package io.github.jeremgamer.maintenancemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public abstract class MaintenanceUtils {

	private static boolean backingUp = false;
	private static ArrayList<String> disabledPlugins = new ArrayList<String>();

	private static final String[] LIBRARIES = {"sigar-x86-winnt.dll" , "sigar-amd64-winnt.dll" , "libsigar-amd64-linux.so" , "libsigar-x86-linux.so" , "libsigar-universal-macosx.dylib" , "libsigar-universal64-macosx.dylib"}; 

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
			sender.sendMessage( MaintenanceManager.getInstance().getCustomConfig().getString("pluginEnabled").replaceAll("&", "§").replaceAll("<plugin>", plugin.getName()) );
		} catch (NullPointerException e1) {
			sender.sendMessage(MaintenanceManager.getInstance().getCustomConfig().getString("pluginManagementArgumentErrorEnable").replaceAll("&", "§"));
		}
	}

	public static void disablePlugin(CommandSender sender , final String toDisable) {
		try {
			Plugin plugin = getPluginIgnoreCase(toDisable);
			Bukkit.getPluginManager().disablePlugin(plugin);
			disabledPlugins.add(plugin.getName());
			MaintenanceManager.getInstance().getCustomConfig().set("disabledPlugins", disabledPlugins);            	 
			MaintenanceManager.getInstance().saveConfig();
			sender.sendMessage( MaintenanceManager.getInstance().getCustomConfig().getString("pluginDisabled").replaceAll("&", "§").replaceAll("<plugin>", plugin.getName()) );
		} catch (NullPointerException e1) {
			sender.sendMessage(MaintenanceManager.getInstance().getCustomConfig().getString("pluginManagementArgumentErrorDisable").replaceAll("&", "§"));
		}
	}

	public static void backup(CommandSender sender) {
		if (backingUp == false) {
			backingUp = true;
			String folderPath = new File("").getAbsolutePath() + "/backups/";
			File folder = new File(folderPath);
			if (!folder.exists()) {
				if (folder.mkdir())
					MaintenanceManager.getInstance().getLogger().info("Backup folder created!");
				else
					MaintenanceManager.getInstance().getLogger().warning("Failed to create backup folder...");
			}

			MaintenanceManager.getInstance().getServer().broadcastMessage( MaintenanceManager.getInstance().getCustomConfig().getString("backingUpMessage").replaceAll("&", "§") );
			MaintenanceManager.getInstance().getLogger().info("Backing up...");


			MaintenanceManager.getInstance().getServer().dispatchCommand(MaintenanceManager.getInstance().getServer().getConsoleSender(), "save-all");
			MaintenanceManager.getInstance().getServer().dispatchCommand(MaintenanceManager.getInstance().getServer().getConsoleSender(), "save-off");

			backupProcess();
			if(sender != null)
				sender.sendMessage( MaintenanceManager.getInstance().getCustomConfig().getString("backupSuccess").replaceAll("&", "§") );

			MaintenanceManager.getInstance().getServer().dispatchCommand(MaintenanceManager.getInstance().getServer().getConsoleSender(), "save-on");
			backingUp = false;
		} else {
			if(sender != null)
				sender.sendMessage( MaintenanceManager.getInstance().getCustomConfig().getString("alreadyBackingUp").replaceAll("&", "§") );
		}
	}

	private static void backupProcess()  {

		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd - HH mm ss");
		String zipFile = new File("").getAbsolutePath() + "/backups/" + dateFormat.format(cal.getTime()) + ".zip";
		File zip = new File(zipFile);
		String srcDir = new File("").getAbsolutePath();

		try {

			try {
				zip.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			File dir = new File(srcDir);

			List<File> filesList = (List<File>) FileUtils.listFilesAndDirs(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
			File[] files = filesList.toArray(new File[filesList.size()]);


			OutputStream out = new FileOutputStream(zip);
			ArchiveOutputStream zipOutput = new ZipArchiveOutputStream(out);
			String filePath;

			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {

				} else  if (files[i].getAbsolutePath().contains(new File("").getAbsolutePath() + "\\backups\\")){

				} else {

					filePath = files[i].getAbsolutePath().substring(srcDir.length() + 1);

					try {	
						ZipArchiveEntry entry = new ZipArchiveEntry(filePath);
						entry.setSize(new File(files[i].getAbsolutePath()).length());
						zipOutput.putArchiveEntry(entry);
						IOUtils.copy(new FileInputStream(files[i].getAbsolutePath()), zipOutput); 
						zipOutput.closeArchiveEntry();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}				
			}
			zipOutput.finish();  
			zipOutput.close();  
			out.close(); 
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (IllegalArgumentException iae) {    			
			iae.printStackTrace();
		}


		MaintenanceManager.getInstance().getLogger().info("Backup success!");
	}

	public static int getCpuUsage() throws SigarException {

		Sigar sigar = new Sigar();
		CpuPerc cpu = sigar.getCpuPerc();
		double cpuUsage = cpu.getUser() * 100 + cpu.getSys() * 100 ;
		int display = (int)cpuUsage;
		return display;
	}

	public static int getMemUsagePercent() throws SigarException {

		Sigar sigar = new Sigar();
		Mem mem = null;
		try {
			mem = sigar.getMem();
		} catch (SigarException se) {
			se.printStackTrace();
		}
		double MEMUse = mem.getUsedPercent();
		int displayPercent = (int) MEMUse;
		return displayPercent;
	}
	public static long getMemUsage() throws SigarException {

		Sigar sigar = new Sigar();
		Mem mem = null;
		try {
			mem = sigar.getMem();
		} catch (SigarException se) {
			se.printStackTrace();
		}
		long MEMUse = mem.getUsed() / 1024 / 1024;
		return MEMUse;
	}

	public static boolean loadLibraries() throws IOException {
		for(String s : LIBRARIES) {
			File lib = new File(new File("").getAbsolutePath() + "/" + s);		
			InputStream input = MaintenanceManager.class.getResourceAsStream(s);
			if(input == null)
				return false;
			OutputStream output = new FileOutputStream(lib);
			byte[] buf = new byte[8192];
			int len;
			while ( (len=input.read(buf)) > 0 ) {
				output.write(buf, 0, len);
			}
			output.flush();
			input.close();
			output.close();
		}
		return true;
	}

}
