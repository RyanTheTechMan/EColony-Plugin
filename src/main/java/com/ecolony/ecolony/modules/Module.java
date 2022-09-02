package com.ecolony.ecolony.modules;

import com.ecolony.ecolony.Main;
import com.ecolony.ecolony.utilities.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.logging.Level;

public interface Module {
    String name();
    String description();
    boolean start();
    boolean stop();
    PluginConfig config();

    void initConfig();

    default void initModule() {
        Bukkit.getLogger().log(Level.INFO, "[Ecolony] Module added: " + name());
        Main.instance.modules.put(id(), this);
        initConfig();
        if (isEnabled()) start();
    }

    default boolean isEnabled() {return Main.instance.config.getConfig().getBoolean("Modules." + id());}

    default String id() {
        String id = name().replaceAll("()([A-Z])", "$1_$2").toLowerCase();
        if (id.startsWith("_")) id = id.substring(1);
        return id;
    }

    boolean onCommand(CommandSender sender, String[] args);
    List<String> onTabComplete(CommandSender sender, String[] args);

    default PluginConfig generateConfig() {
        return new PluginConfig(Main.instance, "modules", id());
    }
}
