package com.ecolony.ecolony;

import com.ecolony.ecolony.Commands.EcolonyCommand;
import com.ecolony.ecolony.modules.AutoCenter.AutoCenter;
import com.ecolony.ecolony.modules.Generator.Generator;
import com.ecolony.ecolony.modules.Mine.Mine;
import com.ecolony.ecolony.utilities.ItemRarityGUI.ItemRarityGUI;
import com.ecolony.ecolony.utilities.Module;
import com.ecolony.ecolony.utilities.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;

public final class Main extends JavaPlugin {
    public static Main instance;
    public BukkitScheduler scheduler;
    public PluginConfig config;
    public final HashMap<String, Module> modules = new HashMap<>();

    public static final String prefix = "" + ChatColor.BOLD + ChatColor.AQUA + "[" + ChatColor.DARK_PURPLE + "E-Colony" + ChatColor.AQUA + "]" + ChatColor.RESET + " ";
    @Override
    public void onEnable() {
        Main.instance = this;
        scheduler = Bukkit.getServer().getScheduler();
        setupModules();
        addCommands();
        addListeners();
    }

    private void addListeners() {
        getServer().getPluginManager().registerEvents(new Mine(), this);
        getServer().getPluginManager().registerEvents(new ItemRarityGUI(), this);
    }

    @Override
    public void onDisable() {
    }
    private void makeModuleConfig(final Module module, final boolean defaultOn) {
        config.setDefault("Modules." + module.id(), defaultOn);
    }

    public Module getModule(String moduleID) {return modules.get(moduleID);}
    private void addCommands() {
        getCommand("ecolony").setExecutor(new EcolonyCommand());
        getCommand("ecolony").setTabCompleter(new EcolonyCommand());
    }
    private void setupModules() {
        config = new PluginConfig(this, "config");

        makeModuleConfig(new AutoCenter(), false);
        makeModuleConfig(new Generator(), false);
        makeModuleConfig(new Mine(), false);

        config.saveConfig();
    }
}