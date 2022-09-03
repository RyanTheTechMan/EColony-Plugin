package com.ecolony.ecolony.modules;

import com.ecolony.ecolony.Main;
import com.ecolony.ecolony.utilities.PluginConfig;
import com.ecolony.ecolony.utilities.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class Generator extends Utilities implements Module {

    private PluginConfig config;
    private List<ItemGenerator> generators;

    public Generator() {
        initModule();
    }

    @Override
    public @NotNull String name() {return "Generator";}

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
    //                        0
    //ec module AutoCenter settings
    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Main.prefix + ChatColor.RED + "Invalid syntax.");
            return true;
        }
        else {
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length != 2) {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "Invalid syntax.");
                } else {
                    ItemGenerator newGen = new ItemGenerator(makeId());
                    newGen.setName(args[1]);
                    newGen.saveConfig();
                    generators.add(newGen);
                    sender.sendMessage(Main.prefix + "A new generator " + ChatColor.BOLD + "(ID#" + newGen.getId() + ")" + ChatColor.RESET + " has been created. Name was set to: " + ChatColor.BOLD + newGen.getName() + ChatColor.RESET + ".");
                }
                return true;
            }
            ItemGenerator selectedGenerator = null;
            for (ItemGenerator g : generators) {
                if (g.getName().equalsIgnoreCase(args[0])) {
                    selectedGenerator = g;
                    break;
                }
            }
            if (selectedGenerator == null) {
                sender.sendMessage(Main.prefix + ChatColor.RED + "Invalid generator.");
                return true;
            }
            if (args.length <= 3) {
                if (args[1].equalsIgnoreCase("fallTime")) {
                    if (args.length == 2) {
                        sender.sendMessage(Main.prefix + "Generator " + selectedGenerator.getName() + " fall time is " + selectedGenerator.getGeneratorFallTime() + ".");
                    }
                    else {
                        int fallTime;
                        try {
                            fallTime = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(Main.prefix + ChatColor.RED + "Invalid fall time.");
                            return true;
                        }
                        selectedGenerator.setGeneratorFallTime(fallTime);
                        sender.sendMessage(Main.prefix + "Generator " + selectedGenerator.getName() + " fall time set to " + fallTime + ".");
                    }
                } else if (args[1].equalsIgnoreCase("name")) {
                    if (args.length == 2){
                        sender.sendMessage(Main.prefix + "Generator name is " + selectedGenerator.getName() + ".");
                    } else {
                        selectedGenerator.setName(args[2]);
                        sender.sendMessage(Main.prefix + "Generator name set to " + selectedGenerator.getName() + ".");
                    }
                }
            }
        }
        return true;
    }

    private boolean testNewID(int id) {
        for (int i : generators.stream().mapToInt(value -> Integer.parseInt(value.getId())).toArray()) {
            if (i == id) {
                return false;
            }
        }
        return true;
    }

    private String makeId() {
        int out = 0;
        while (!testNewID(out)) {
            out++;
        }
        return String.valueOf(out);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    private List<ItemGenerator> getAllItemGenerators() {
        List<ItemGenerator> list = new ArrayList<>();
        ConfigurationSection configurationSection = config.getConfig().getConfigurationSection("Generators.id");
        if (configurationSection == null) {
            return list;
        }
        String[] ids = configurationSection.getKeys(false).toArray(new String[0]);
        for (String id : ids) {
            ItemGenerator gen = new ItemGenerator(id);
            gen.readConfig();
            list.add(gen);
        }
        return list;
    }
}
