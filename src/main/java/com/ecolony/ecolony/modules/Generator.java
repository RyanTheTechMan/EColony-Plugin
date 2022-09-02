package com.ecolony.ecolony.modules;

import com.ecolony.ecolony.Main;
import com.ecolony.ecolony.utilities.Particles.Cube;
import com.ecolony.ecolony.utilities.Particles.Vortex;
import com.ecolony.ecolony.utilities.PluginConfig;
import com.ecolony.ecolony.utilities.Utilities;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Generator extends Utilities implements Module {

    private PluginConfig config;
    private List<ItemGenerator> generators;

    public Generator() {
        initModule();
    }

    @Override
    public String name() {return "Generator";}

    @Override
    public String description() {return "Spawns items at specified locations based on rarity.";}

    @Override
    public boolean start() {
        generators = getAllItemGenerators();
        return true;
    }

    @Override
    public boolean stop() {
        for (ItemGenerator generator : generators) {
            generator.stop();
        }
        return true;
    }

    @Override
    public PluginConfig config() {return config;}

    @Override
    public void initConfig() {
        config = generateConfig();
        config.saveConfig();
    }
    //0    1     2            3
    //                        0
    //ec module AutoCenter settings
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Invalid syntax.");
            return true;
        }
        else if (args.length == 1) {

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    private List<ItemGenerator> getAllItemGenerators() {
        List<ItemGenerator> list = new ArrayList<>();
        String[] ids = config.getConfig().getConfigurationSection("Generators.id").getKeys(false).toArray(new String[0]);
        for (String id : ids) {
            ItemGenerator gen = new ItemGenerator(id);
            gen.readConfig();
            list.add(gen);
        }
        return list;
    }
}
