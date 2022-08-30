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
            sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Incomplete command.");
        }
        else if (args.length == 1) {
            StringBuilder out = new StringBuilder();
            for (Module m : Main.instance.modules.values()) {
                out.append(" - ").append(ChatColor.RESET).append(m.isEnabled() ? ChatColor.GREEN : ChatColor.RED).append(m.name()).append(ChatColor.RESET).append("\n");
            }
            out.delete(out.length()-2,out.length()-1);
            sender.sendMessage(Main.prefix + "Modules:\n" + out);
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("module")) {
                final Module m = Main.instance.getModule(args[1]);
                if (Main.instance.getModule(args[1]) == null) {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid module name.");
                }
                else {
                    sender.sendMessage(Main.prefix + "Module info:\n" +
                            ChatColor.BOLD + "Name: " + ChatColor.RESET + m.name() + "\n" +
                            ChatColor.BOLD + "Enabled: " + ChatColor.RESET + (m.isEnabled() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No") + ChatColor.RESET + "\n" +
                            ChatColor.BOLD + "Description: " + ChatColor.RESET + Main.instance.getModule(args[1]).description());
                }
            }
        }
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("module")) {
                Module m = Main.instance.getModule(args[1]);
                if (m == null) {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid module name.");
                } else {
                    if (args[2].equalsIgnoreCase("enable")) {
                        Main.instance.config.getConfig().set("Modules." + m.id(), true);
                        Main.instance.config.saveConfig();
                        m.start();
                        sender.sendMessage(Main.prefix + ChatColor.GREEN + "Module " + m.name() + " enabled.");
                    } else if (args[2].equalsIgnoreCase("disable")) {
                        Main.instance.config.getConfig().set("Modules." + m.id(), false);
                        Main.instance.config.saveConfig();
                        m.stop();
                        sender.sendMessage(Main.prefix + ChatColor.DARK_RED + "Module " + m.name() + " disabled.");
                    } else {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid option!");
                    }
                }
            }
            else {
                sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid argument!");
            }
        }
        else {
            if (args[0].equalsIgnoreCase("module")) {
                Module m = Main.instance.getModule(args[1]);
                if (m == null) {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid module name.");
                } else {
                    m.onCommand(sender, command, label, args);
                }
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
                final Module m = Main.instance.getModule(args[1]);
                if (m != null) {
                    ArrayList<String> arguments = new ArrayList<>();
                    if (m.isEnabled()) {
                        arguments.add("disable");
                    }
                    else {
                        arguments.add("enable");
                    }
                    arguments.add("setting");
                    return arguments;
                }
            }
        }
        else {
            if (args[0].equalsIgnoreCase("module")) {
                final Module m = Main.instance.getModule(args[1]);
                if (m != null) {
                    return m.onTabComplete(sender, command, alias, args);
                }
            }
        }
        return List.of();
    }

}