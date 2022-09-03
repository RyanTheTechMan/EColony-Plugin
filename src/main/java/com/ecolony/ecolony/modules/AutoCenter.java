package com.ecolony.ecolony.modules;

import com.ecolony.ecolony.Exceptions.InvalidSenderTypeException;
import com.ecolony.ecolony.Exceptions.PlayerNotFoundException;
import com.ecolony.ecolony.Exceptions.WorldNameException;
import com.ecolony.ecolony.Main;
import com.ecolony.ecolony.utilities.PluginConfig;
import com.ecolony.ecolony.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class AutoCenter extends Utilities implements Module {
    private PluginConfig config;

    @Override
    public @NotNull String name() {return "AutoCenter";}

    @Override
    public String description() {return "Automatically centers players to the specified location when falling.";}

    @Override
    public PluginConfig config() {return config;}

    @Override
    public void initConfig() {
        config = generateConfig();
        config.setDefault("Location", new Location(Main.instance.getServer().getWorlds().get(0), 0, 0, 0));
        config.setDefault("Speed", 1d);
        config.setDefault("Radius", 10d);
        config.setDefault("StartBelowHeight", false);
        config.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setting")) {
                if (args[1].equalsIgnoreCase("speed")) {
                    sender.sendMessage(Main.prefix + name() + " speed is: " + config.getConfig().getDouble("Speed"));
                }
                else if (args[1].equalsIgnoreCase("radius")) {
                    sender.sendMessage(Main.prefix + name() + " radius is: " + config.getConfig().getDouble("Radius"));
                }
                else if (args[1].equalsIgnoreCase("startbelowheight")) {
                    sender.sendMessage(Main.prefix + name() + " start below height is: " + config.getConfig().getBoolean("StartBelowHeight"));
                }
                else if (args[1].equalsIgnoreCase("location")) {
                    sender.sendMessage(Main.prefix + "Current location: " + getLocationString(config.getConfig().getLocation("Location")));
                }
                else {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid argument!");
                }
            } else {
                sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid argument!");
            }
        } else if (args.length >= 3 && args.length <= 6) {
            if (args[0].equalsIgnoreCase("setting")) {
                if (args[1].equalsIgnoreCase("speed")) {
                    if (args.length == 3) {
                        config.getConfig().set("Speed", Double.parseDouble(args[2]));
                        config.saveConfig();
                        sender.sendMessage(Main.prefix + "Speed set to " + args[2] + ".");
                        start();
                    } else {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid speed! Must be a number!");
                    }
                }
                else if (args[1].equalsIgnoreCase("radius")){
                    if (args.length == 3) {
                        config.getConfig().set("Radius", Double.parseDouble(args[2]));
                        config.saveConfig();
                        sender.sendMessage(Main.prefix + "Radius set to " + args[2] + ".");
                        start();
                    } else {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid radius! Must be a number!");
                    }
                }
                else if (args[1].equalsIgnoreCase("startbelowheight")){
                    if (args.length == 3) {
                        final boolean b = Boolean.parseBoolean(args[2]);
                        config.getConfig().set("StartBelowHeight", Boolean.parseBoolean(args[2]));
                        config.saveConfig();
                        sender.sendMessage(Main.prefix + "StartBelowHeight set to " + (b ? (ChatColor.GREEN + "TRUE") : (ChatColor.RED + "FALSE")) + ChatColor.RESET + ".");
                        start();
                    } else {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid StartBelowHeight! Must be a boolean!");
                    }
                }
                else if (args[1].equalsIgnoreCase("location")) {
                    Location loc = getLocationOnCommandReportError(sender, Arrays.copyOfRange(args, 2, args.length));
                    if (loc != null) {
                        config.getConfig().set("Location", loc);
                        config.saveConfig();
                        sender.sendMessage(Main.prefix + "Location set to " + getLocationString(loc) + ".");
                        start();
                    }
                    else {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid location!");
                    }
                } else {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid argument!");
                }
            } else {
                sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid argument!");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setting")) {
                return List.of("location", "speed", "radius", "startbelowheight");
            }
        }
        else if (args[1].equalsIgnoreCase("location") && args.length <= 6 && sender instanceof Player p) {
            List<String> worldNames = new ArrayList<>();
            Main.instance.getServer().getWorlds().forEach(w -> worldNames.add(w.getName()));
            if (args.length == 3) {
                ArrayList<String> arguments = new ArrayList<>();
                arguments.add(String.valueOf(Math.floor(p.getLocation().getX()) + 0.5));
                arguments.add("player");
                arguments.addAll(worldNames);
                return arguments;
            } else if (args.length == 4) {
                ArrayList<String> arguments = new ArrayList<>();
                if (args[2].equalsIgnoreCase("player")) {
                    Main.instance.getServer().getOnlinePlayers().forEach(pl -> {
                        if (pl.getName().equalsIgnoreCase(sender.getName())) {
                            arguments.add(0, pl.getName());
                        } else {
                            arguments.add(pl.getName());
                        }
                    });
                } else if (worldNames.contains(args[2])) {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getX()) + 0.5));
                } else {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getY())));
                }
                return arguments;
            } else if (args[2].equalsIgnoreCase("player")) {
                return List.of();
            } else if (args.length == 5) {
                ArrayList<String> arguments = new ArrayList<>();
                if (worldNames.contains(args[2])) {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getY())));
                } else {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getZ()) + 0.5));
                }
                return arguments;
            } else if (args.length == 6) {
                ArrayList<String> arguments = new ArrayList<>();
                if (worldNames.contains(args[2])) {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getZ()) + 0.5));
                }
                return arguments;
            }
        }
        return List.of();
    }

    private BukkitTask task;
    public AutoCenter() {
        initModule();
    }

    private BukkitRunnable buildTask() {
        final Location centerPosition = config.getConfig().getLocation("Location"); //TODO: Make this send an error if the location is invalid
        final float speed = (float)config.getConfig().getDouble("Speed");
        final double radius = config.getConfig().getDouble("Radius");
        final boolean startBelowHeight = config.getConfig().getBoolean("StartBelowHeight");
        return new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getOnlinePlayers().forEach(p -> {
                    final Vector velocity = p.getVelocity();
                    final Location loc = p.getLocation();
                    if (velocity.getY() < -1 && (Math.abs(loc.getX() - centerPosition.getX()) > radius || Math.abs(loc.getZ() - centerPosition.getZ()) > radius) && (!startBelowHeight || loc.getY() < centerPosition.getY())){
                        velocity.setX(velocity.getX()+speed*0.03*-clamp((float)(loc.getX() - centerPosition.getX()), -speed, speed));
                        velocity.setZ(velocity.getZ()+speed*0.03*-clamp((float)(loc.getZ() - centerPosition.getZ()), -speed, speed));
                        p.setVelocity(velocity);
                    }
                });
            }
        };
    }

    @Override
    public boolean start() {
        if (task != null && !task.isCancelled()) task.cancel();
        final BukkitRunnable runnable = buildTask();

        task = runnable.runTaskTimer(Main.instance, 0, 1);

        Bukkit.getLogger().log(Level.INFO, "[Ecolony] Module started: " + name());
        return !task.isCancelled();
    }

    @Override
    public boolean stop() {
        if (!task.isCancelled()) {
            task.cancel();
            task = null;
        }
        Bukkit.getLogger().log(Level.INFO, "[Ecolony] Module stopped: " + name());
        return true;
    }
}
