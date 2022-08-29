package com.ecolony.ecolony.utilities;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class PluginConfig {

	private Plugin plugin;
	private File file;
	private File folder;
	private YamlConfiguration config;


	public PluginConfig(Plugin plugin, String folder, String name) {
		this.folder = new File(plugin.getDataFolder(), folder);
		this.plugin = plugin;


		try {
			this.folder.mkdir();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.file = new File(this.folder, name + ".yml");
		try {
			file.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

		reloadConfig();

	}

	public PluginConfig(Plugin plugin, String name, boolean copy) {
		this.plugin = plugin;
		this.file = new File(plugin.getDataFolder(), name + ".yml");

		if (copy) {

			plugin.saveResource(file.getName(), true);

		} else {

			try {
				file.createNewFile();
			} catch (Exception e) {

				e.printStackTrace();

			}

		}
		reloadConfig();

	}

	public PluginConfig(Plugin plugin, String name) {
		this.plugin = plugin;
		this.file = new File(plugin.getDataFolder(), name + ".yml");
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		reloadConfig();
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(file);
	}

	public void saveConfig() {

		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDefault(final String path, final Object _default) {
		if (config.get(path) == null) config.set(path, _default);
	}
}