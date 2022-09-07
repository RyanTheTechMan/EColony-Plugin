package com.ecolony.ecolony.modules.Generator;

import com.ecolony.ecolony.Main;
import com.ecolony.ecolony.utilities.Module;
import com.ecolony.ecolony.utilities.PluginConfig;
import com.ecolony.ecolony.utilities.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;


public class Generator extends Utilities implements Module {
    private PluginConfig config;
    private List<ItemGenerator> generators;

    public Generator() {
        initModule();
    }

    @Override
    public @NotNull String name() {return "Generator";}

    @Override
    public @NotNull String description() {return "Spawns items at specified locations based on rarity.";}

    @Override
    public boolean start() {
        Main.instance.getLogger().log(Level.WARNING, "HEY!!!!!!!!!!! Called Start");
        Main.instance.getLogger().log(Level.WARNING, "HEY!!!!!!!!!!! Found " + generators.size() + " generators.");
        for (ItemGenerator generator : generators) {
            generator.start();
        }
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
        generators = getAllItemGenerators();
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return true;
        }
        if (args[0].equalsIgnoreCase("enable")||args[0].equalsIgnoreCase("disable")) return true;
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length != 2) {
                sender.sendMessage(Main.prefix + ChatColor.RED + "Invalid syntax.");
            } else {
                ItemGenerator newGen = new ItemGenerator(makeId());
                newGen.setName(args[1]);
                generators.add(newGen);
                sender.sendMessage(Main.prefix + "A new generator " + ChatColor.BOLD + "(ID#" + newGen.getId() + ")" + ChatColor.RESET + " has been created. Name was set to: " + ChatColor.BOLD + newGen.getName() + ChatColor.RESET + ".");
            }
            return true;
        }

        ItemGenerator selectedGenerator = getGenerator(args[0]);
        if (selectedGenerator == null) {
            sender.sendMessage(Main.prefix + ChatColor.RED + "Invalid generator.");
            return true;
        }
        if (args.length == 1) {
            sender.sendMessage(Main.prefix + ChatColor.BOLD + "Generator Information:" + ChatColor.RESET + "\n" +
                    ChatColor.BOLD + "Name: " + ChatColor.RESET + selectedGenerator.getName() + "\n" +
                    ChatColor.BOLD + "ID: " + ChatColor.RESET + selectedGenerator.getId() + "\n" +
                    ChatColor.BOLD + "Enabled: " + ChatColor.RESET + selectedGenerator.isEnabled() + "\n" +
                    ChatColor.BOLD + "Location: " + ChatColor.RESET + selectedGenerator.getLocation().toString() + "\n" +
                    ChatColor.BOLD + "Items: " + ChatColor.RESET + Arrays.toString(selectedGenerator.getItems().toArray()));
        }
        else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("location")) {
                sender.sendMessage(Main.prefix + "Generator " + selectedGenerator.getName() + " location is " + getLocationString(selectedGenerator.getLocation()) + ".");
            }
            else if (args[1].equalsIgnoreCase("fallTime")) {
                sender.sendMessage(Main.prefix + "Generator " + selectedGenerator.getName() + " fall time is " + selectedGenerator.getFallTime() + ".");
            } else if (args[1].equalsIgnoreCase("spawnTime")) {
                sender.sendMessage(Main.prefix + "Generator " + selectedGenerator.getName() + " spawn time is " + selectedGenerator.getSpawnTime() + ".");
            }
            else if (args[1].equalsIgnoreCase("name")) {
                sender.sendMessage(Main.prefix + "Generator name is " + selectedGenerator.getName() + ".");
            } else if (args[1].equalsIgnoreCase("enable")) {
                boolean okayToRun = true;
                if (selectedGenerator.getLocation() == null) {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "Generator location is not set.");
                    okayToRun = false;
                }
                if (selectedGenerator.getFallTime() <= 0) {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "Generator fall time is not set.");
                    okayToRun = false;
                }
                if (selectedGenerator.getSpawnTime() <= 0) {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "Generator spawn time is not set.");
                    okayToRun = false;
                }
                if (selectedGenerator.getItems().isEmpty()) {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "WARNING! Generator items are not set. Using random items.");
                }
                if (okayToRun) {
                    selectedGenerator.setEnabled(true);
                    selectedGenerator.start();
                    sender.sendMessage(Main.prefix + "Generator " + selectedGenerator.getName() + " has been enabled.");
                }
            } else if (args[1].equalsIgnoreCase("disable")) {
                selectedGenerator.setEnabled(false);
                selectedGenerator.stop();
                sender.sendMessage(Main.prefix + "Generator " + selectedGenerator.getName() + " has stopped.");
            } else if (args[1].equalsIgnoreCase("remove")) {
                selectedGenerator.stop();
                config.getConfig().set("Generators." + selectedGenerator.getId(), null);
                config.saveConfig();
                generators.remove(selectedGenerator);
                sender.sendMessage(Main.prefix + "Generator " + selectedGenerator.getName() + " has been REMOVED.");
            } else {
                sender.sendMessage(Main.prefix + ChatColor.RED + "Invalid syntax.");
            }
        }
        else if (args.length >= 3) {
            if (args[1].equalsIgnoreCase("fallTime")) {
                int fallTime;
                try {
                    fallTime = Integer.parseInt(args[2]);
                    if (fallTime <= 0) {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "Fall time must be a positive integer.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "Invalid fall time.");
                    return true;
                }
                selectedGenerator.setFallTime(fallTime);
                sender.sendMessage(Main.prefix + "Generator " + selectedGenerator.getName() + " fall time set to " + fallTime + ".");
            } else if (args[1].equalsIgnoreCase("spawnTime")) {
                int spawnTime;
                try {
                    spawnTime = Integer.parseInt(args[2]);
                    if (spawnTime <= 0) {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "Fall time must be a positive integer.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "Invalid spawn time.");
                    return true;
                }
                selectedGenerator.setSpawnTime(spawnTime);
                sender.sendMessage(Main.prefix + "Generator " + selectedGenerator.getName() + " spawn time set to " + spawnTime + ".");
            } else if (args[1].equalsIgnoreCase("name")) {
                selectedGenerator.setName(args[2]);
                sender.sendMessage(Main.prefix + "Generator name set to " + selectedGenerator.getName() + ".");
            }
            else if (args.length <= 6) {
                if (args[1].equalsIgnoreCase("location")) {
                    final Location loc = getLocationOnCommandReportError(sender, Arrays.copyOfRange(args, 2, args.length));
                    if (loc == null) {
                        return true;
                    }
                    selectedGenerator.setLocation(loc);
                    sender.sendMessage(Main.prefix + "Generator " + selectedGenerator.getName() + " location set to " + getLocationString(selectedGenerator.getLocation()) + ".");
                }
            } else {
                sender.sendMessage(Main.prefix + ChatColor.RED + "Invalid syntax.");
            }
        }
        else {
            sender.sendMessage(Main.prefix + ChatColor.RED + "Invalid syntax.");
        }
        return true;
    }


    /**
        * @param generator The generator name or id to search for. If it is an id, it must be prefixed with "id:"
        * @return The generator
     */
    @Nullable
    private ItemGenerator getGenerator(@NotNull String generator) { //todo: return a list of generators if the name is not unique
        ItemGenerator foundGenerator = null;
        for (ItemGenerator g : generators) {
            if (generator.toLowerCase().startsWith("id:")) {
                final String id = generator.toLowerCase().replace("id:", "");
                if (g.getId().equals(id)) {
                    foundGenerator = g;
                    break;
                }
            }
            else if (g.getName().equalsIgnoreCase(generator)) {
                foundGenerator = g;
                break;
            }
        }
        return foundGenerator;
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
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>(List.of("create"));
            if (args[0].toLowerCase().startsWith("id:")) {
                arguments.addAll(generators.stream().filter(g -> g.getId().startsWith(args[0].toLowerCase().replace("id:", ""))).map(g -> "id:" + g.getId()).toList());
            }
            else arguments.addAll(generators.stream().map(ItemGenerator::getName).toList());
            return arguments;
        } else if (args.length == 2) {
            final List<String> arguments = new ArrayList<>();
            final ItemGenerator gen = getGenerator(args[0]);
            if (gen == null) {
                return arguments;
            } else if (gen.isEnabled()) {
                arguments.add("disable");
            } else {
                arguments.add("enable");
            }
            arguments.addAll(Arrays.asList("location", "fallTime", "spawnTime", "name", "remove"));
            return arguments;
        }
        else if (args[1].equalsIgnoreCase("location")) {
            return getLocationTabComplete(sender, Arrays.copyOfRange(args, 2, args.length));
        }
        return List.of("");
    }

    private List<ItemGenerator> getAllItemGenerators() {
        List<ItemGenerator> list = new ArrayList<>();
        ConfigurationSection configurationSection = config.getConfig().getConfigurationSection("Generators");
        if (configurationSection == null) {
            Main.instance.getLogger().log(Level.WARNING,"There are no IDs");
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