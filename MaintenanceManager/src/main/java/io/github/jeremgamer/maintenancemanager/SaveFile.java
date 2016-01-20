/**
 *   Copyright 2014/2015 JeremGamer
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. * 
 */

package io.github.jeremgamer.maintenancemanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class SaveFile {

	ArrayList<String> content = new ArrayList<String>();

	public int getLineNumber() {
		return content.size();
	}

	public boolean containsSection(String section) {
		for (String line : content) {
			if (line.startsWith(section + ": ")) {
				return true;
			}
		}
		return false;
	}
	
	public int indexOf(String section) {
		for (String line : content)
			if(line.startsWith(section + ": "))
				return content.indexOf(line);
		return -1;
	}

	public void createSection(String name , Object value) {
		content.add("");
		if (value instanceof String)
			content.add(name + ": '" + value + "'");
		else
			content.add(name + ": " + value);
	}
	
	public void createSection(int index , String name , Object value) {	
		content.add(index , "");
		if (value instanceof String)
			content.add(index+1 , name + ": '" + value + "'");
		else
			content.add(index+1 , name + ": " + value);
	}

	public void removeSection(String name) {
		for (String line : content) {
			if(line.startsWith(name + ": ")) {
				content.remove(content.indexOf(line)-1);
				content.remove(line);
				break;
			}
		}
	}

	public void set(String section , Object value) {
		int id = 0;
		for (String line : content) {
			if (line.startsWith(section)) {
				if (value instanceof String) {
					content.set(id , section + ": '" + value + "'");
					return;
				} else {
					content.set(id, section + ": " + value);
					return;
				}
			}
			id++;
		}
		if(!containsSection(section))
			createSection(section , value);
	}

	public String getString(String section) {
		String value = "";
		for (String line : content) {
			if (line.startsWith(section + ": '")) {
				value = line.substring(section.length() + 3);
				return value.substring(0, value.length()-1);
			}
		}
		MaintenanceManager.getInstance().getLogger().warning("Invalid value in \"" + section + "\" config section.");
		return "";
	}

	public int getInt(String section) {
		for (String line : content) {
			if (line.startsWith(section + ": ")) {
				try {
					int value = Integer.parseInt(line.substring(section.length() + 2));
					return value;
				} catch (NumberFormatException nfe) {
					MaintenanceManager.getInstance().getLogger().warning("Invalid value in \"" + section + "\" config section. Please don't modify this line.");
					set(section , 0);
					MaintenanceManager.getInstance().saveConfig();
					return -1;
				}
			}
		}
		return -1;
	}

	public boolean getBoolean(String section) {
		for (String line : content) {
			if (line.startsWith(section + ": ")) {
				boolean value = Boolean.parseBoolean(line.substring(section.length() + 2));
				return value;
			}
		}
		return false;
	}

	public List<String> getStringList(String section) {
		for (String line : content) {
			if (line.startsWith(section + ": ")) {
				try {
					String raw = line.substring(section.length() + 2);
					@SuppressWarnings("unchecked")
					ArrayList<String> list = new Gson().fromJson(raw, ArrayList.class);
					return list;
				} catch (JsonSyntaxException ise) {
					MaintenanceManager.getInstance().getLogger().warning("Invalid value in \"" + section + "\" config section. Please don't modify this line.");
					set(section , new ArrayList<String>());
					MaintenanceManager.getInstance().saveConfig();
					return null;
				}
			}
		}
		return null;
	}

	public void load(File file) throws IOException {
		try {
			FileReader fr = new FileReader(file.getAbsoluteFile());
			BufferedReader br = new BufferedReader(fr);
			content.clear();
			String line = br.readLine();
			while (line != null) {
				content.add(line);
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException fnfe) {}
	}

	public void save(File file) throws IOException {
		if (file.getName().equals("config.yml")) {
			if (!file.exists()) {
				File folders = new File(file.getParent());
				folders.mkdirs();
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (String line : content) {
				bw.write(line + "\n");
			}
			bw.flush();
			bw.close();
			fw.close();
		}
	}
}
