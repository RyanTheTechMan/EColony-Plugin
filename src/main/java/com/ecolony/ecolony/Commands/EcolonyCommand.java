package com.ecolony.ecolony.Commands;

import com.ecolony.ecolony.Main;
import com.ecolony.ecolony.modules.AutoCenter;
import com.ecolony.ecolony.modules.Module;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EcolonyCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            sender.sendMessage(Main.prefix + "You ran /ecolony");
        }
        else if (args.length == 1) { // /ecolony module autocenter
            // show details about the module
            sender.sendMessage(Main.prefix + "Current modules: " + Arrays.toString(Main.instance.modules.values().toArray()));
            String out = "";
            for (Module m : Main.instance.modules.values()) {
                out += m.isEnabled() ? ChatColor.GREEN : ChatColor.RED + m.name() + ", ";
            }
            out = out.substring(0, out.length() - 2);
            sender.sendMessage(Main.prefix + "Modules:\n" + out);
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("module")) {
                if (Main.instance.getModule(args[1]) == null) {
                    sender.sendMessage(Main.prefix + "Error! Invalid module name.");
                }
                else {
                    sender.sendMessage(Main.prefix + "Module info:\n" + Main.instance.getModule(args[1]).name() + " - " + Main.instance.getModule(args[1]).description());
                }
            }
        }
        else if (args.length == 3) { // /ecolony module autocenter enable
            if (args[0].equalsIgnoreCase("module")) {
                Module m = Main.instance.getModule(args[1]);
                if (m == null) {
                    sender.sendMessage(Main.prefix + "Error! Invalid module name.");
                } else {
                    if (args[2].equalsIgnoreCase("enable")) {
                        Main.instance.config.getConfig().set("Modules." + m.id(), true);
                        Main.instance.config.saveConfig();
                        m.start();
                        sender.sendMessage(Main.prefix + "Module " + m.name() + " enabled.");
                    } else if (args[2].equalsIgnoreCase("disable")) {
                        Main.instance.config.getConfig().set("Modules." + m.id(), false);
                        Main.instance.config.saveConfig();
                        m.stop();
                        sender.sendMessage(Main.prefix + "Module " + m.name() + " disabled.");
                    } else {
                        sender.sendMessage(Main.prefix + "Error! Invalid option!");
                    }
                }
            }
            else {
                sender.sendMessage(Main.prefix + "Error! Invalid argument!");
            }
        }
        else if (args.length == 4) {// /ecolony module autocenter set location
            // set is keyword, save data to config where needed
            if (args[0].equalsIgnoreCase("module")) {
                if (Main.instance.getModule(args[1]) == null) { // check if module exists
                    sender.sendMessage("Error! Invalid module name.");
                }

                Module m = Main.instance.getModule(args[1]); // get module

                if (args[2].equalsIgnoreCase("setting")) { // check if set keyword
                    if (m instanceof AutoCenter) {
                        if (args[3].equalsIgnoreCase("location")) { // check if location keyword
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                m.config().getConfig().set("Location", new Location(p.getWorld(), Math.floor(p.getLocation().getX()) + 0.5, Math.floor(p.getLocation().getY()) + 0.5, Math.floor(p.getLocation().getZ()) + 0.5));
                                m.config().saveConfig();
                                sender.sendMessage(Main.prefix + m.name() + " location was set to player location.");
                            } else {
                                sender.sendMessage(Main.prefix + "Error! You must be a player to use this command. You can enter world, x, y, z");
                            }
                        } else {
                            sender.sendMessage(Main.prefix + "Error! Invalid argument!");
                        }
                    }
                } else {
                    sender.sendMessage(Main.prefix + "Error! Invalid argument!");
                }
            }
            else {
                sender.sendMessage(Main.prefix + "Error! Invalid argument!");
            }
        }
        else if (args.length == 7 || args.length == 8){
            if (args[0].equalsIgnoreCase("module")) {
                if (Main.instance.getModule(args[1]) == null) { // check if module exists
                    sender.sendMessage("Error! Invalid module name.");
                }

                Module m = Main.instance.getModule(args[1]); // get module
                if (args[2].equalsIgnoreCase("setting")) {
                    if (m instanceof AutoCenter) {
                        if (args[3].equalsIgnoreCase("location")) {
                            if (args.length == 7) {
                                if (sender instanceof Player) {
                                    Player p = (Player) sender;
                                    Location loc = new Location(p.getWorld(), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
                                    m.config().getConfig().set("Location", loc);
                                    m.config().saveConfig();
                                    sender.sendMessage(Main.prefix + m.name() + " location was set to " + args[4] + ", " + args[5] + ", " + args[6]);
                                } else {
                                    sender.sendMessage(Main.prefix + "Error! You must be a player to use this command. You can enter world, x, y, z");
                                }
                            } else if (args.length == 8) {
                                World w = Main.instance.getServer().getWorld(args[4]);
                                if (w == null) {
                                    sender.sendMessage(Main.prefix + "Error! Invalid world name.");
                                } else {
                                    Location enteredLocation = new Location(w, Double.parseDouble(args[5]), Double.parseDouble(args[6]), Double.parseDouble(args[7]));
                                    m.config().getConfig().set("Location", enteredLocation);
                                    m.config().saveConfig();
                                    sender.sendMessage(Main.prefix + m.name() + " location was set to " + w.getName() + ", " + args[5] + ", " + args[6] + ", " + args[7]);
                                }
                            } else {
                                sender.sendMessage(Main.prefix + "Error! Invalid argument!");
                            }
                        } else {
                            sender.sendMessage(Main.prefix + "Error! Invalid argument!");
                        }
                    }
                } else {
                    sender.sendMessage(Main.prefix + "Error! Invalid argument!");
                }
            }
            else {
                sender.sendMessage(Main.prefix + "Error! Invalid argument!");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1){
            return List.of("module");
        } else if (args.length == 2){
            return Main.instance.modules.keySet().stream().filter(s -> s.toLowerCase().startsWith(args[1])).collect(Collectors.toList());
        }
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("module")) {
                if (Main.instance.getModule(args[1]) != null) {
                    return List.of("enable", "disable", "setting");
                }
            }
        }
        else if (args.length == 4) {
            if (Main.instance.getModule(args[1]) instanceof AutoCenter) {
                if (args[2].equalsIgnoreCase("setting")) {
                    return List.of("location");
                }
            }
        }
        else if (args.length >= 5 && args.length <= 8 && sender instanceof Player) { //TODO: test for instance of AutoCenter
            List<String> worldNames = new ArrayList<>();
            Main.instance.getServer().getWorlds().forEach(w -> {
                worldNames.add(w.getName());
            });
            if (args.length == 5) {
                // /ecolony module autocenter set location world
                // /ecolony module autocenter set location x
                ArrayList<String> arguments = new ArrayList<>();

                arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getX()) + 0.5));

                arguments.addAll(worldNames);
                return arguments;
            }
            else if (args.length == 6) {
                // /ecolony module autocenter set location world x
                // /ecolony module autocenter set location x y

                ArrayList<String> arguments = new ArrayList<>();
                if (worldNames.contains(args[4])) {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getX()) + 0.5));
                }
                else {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getY())));
                }
                return arguments;
            }
            else if (args.length == 7) {
                // /ecolony module autocenter set location world x y
                // /ecolony module autocenter set location x y z
                ArrayList<String> arguments = new ArrayList<>();
                if (worldNames.contains(args[4])) {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getY())));
                }
                else {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getZ()) + 0.5));
                }
                return arguments;
            }
            else if (args.length == 8) {
                // /ecolony module autocenter set location world x y z
                ArrayList<String> arguments = new ArrayList<>();
                if (worldNames.contains(args[4])) {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getZ()) + 0.5));
                }
                return arguments;
            }
        }
        return List.of();
    }
}