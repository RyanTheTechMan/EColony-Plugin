package com.ecolony.ecolony.modules;

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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class AutoCenter extends Utilities implements Module {
    private PluginConfig config;

    @Override
    public String name() {return "AutoCenter";}

    @Override
    public String description() {return "Automatically centers players to the specified location when falling.";}

    @Override
    public PluginConfig config() {return config;}

    @Override
    public void initConfig() {
        config = new PluginConfig(Main.instance, "modules", id());
        config.setDefault("Location", new Location(Main.instance.getServer().getWorlds().get(0), 0, 0, 0));
        config.setDefault("Speed", 1d);
        config.setDefault("Radius", 10d);
        config.setDefault("StartBelowHeight", false);
        config.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 4) {
            if (args[2].equalsIgnoreCase("setting")) {
                if (args[3].equalsIgnoreCase("speed")) {
                    sender.sendMessage(Main.prefix + name() + " speed is: " + config.getConfig().getDouble("Speed"));
                }
                else if (args[3].equalsIgnoreCase("radius")) {
                    sender.sendMessage(Main.prefix + name() + " radius is: " + config.getConfig().getDouble("Radius"));
                }
                else if (args[3].equalsIgnoreCase("startbelowheight")) {
                    sender.sendMessage(Main.prefix + name() + " start below height is: " + config.getConfig().getBoolean("StartBelowHeight"));
                }
                else if (args[3].equalsIgnoreCase("location")) {
                    sender.sendMessage(Main.prefix + "Current location: " + getLocationString(config.getConfig().getLocation("Location")));
                }
                else {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid argument!");
                }
            } else {
                sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid argument!");
            }
        } else if (args.length >= 5 && args.length <= 8) {
            if (args[2].equalsIgnoreCase("setting")) {
                if (args[3].equalsIgnoreCase("speed")) {
                    if (args.length == 5) {
                        config.getConfig().set("Speed", Double.parseDouble(args[4]));
                        config.saveConfig();
                        sender.sendMessage(Main.prefix + "Speed set to " + args[4] + ".");
                        start();
                    } else {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid speed! Must be a number!");
                    }
                }
                else if (args[3].equalsIgnoreCase("radius")){
                    if (args.length == 5){
                        config.getConfig().set("Radius", Double.parseDouble(args[4]));
                        config.saveConfig();
                        sender.sendMessage(Main.prefix + "Radius set to " + args[4] + ".");
                        start();
                    } else {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid radius! Must be a number!");
                    }
                }
                else if (args[3].equalsIgnoreCase("startbelowheight")){
                    if (args.length == 5){
                        final boolean b = Boolean.parseBoolean(args[4]);
                        config.getConfig().set("StartBelowHeight", Boolean.parseBoolean(args[4]));
                        config.saveConfig();
                        sender.sendMessage(Main.prefix + "StartBelowHeight set to " + (b ? (ChatColor.GREEN + "TRUE") : (ChatColor.RED + "FALSE")) + ChatColor.RESET + ".");
                        start();
                    } else {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid StartBelowHeight! Must be a boolean!");
                    }
                }
                else if (args[3].equalsIgnoreCase("location")) {
                    if (args[4].equalsIgnoreCase("player")) {
                        if (args.length == 5) {
                            if (sender instanceof Player p) {
                                config.getConfig().set("Location", new Location(p.getWorld(), Math.floor(p.getLocation().getX()) + 0.5, Math.floor(p.getLocation().getY()) + 0.5, Math.floor(p.getLocation().getZ()) + 0.5));
                                config.saveConfig();
                                sender.sendMessage(Main.prefix + name() + " location was set to player location.");
                                start();
                            } else {
                                sender.sendMessage(Main.prefix + ChatColor.RED + "Error! You must be a player to use this command. You can enter world, x, y, z");
                            }
                        } else if (args.length == 6) {
                            Player p = Bukkit.getPlayer(args[5]);
                            if (p == null) {
                                sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Player not found.");
                            } else {
                                config.getConfig().set("Location", new Location(p.getWorld(), Math.floor(p.getLocation().getX()) + 0.5, Math.floor(p.getLocation().getY()) + 0.5, Math.floor(p.getLocation().getZ()) + 0.5));
                                config.saveConfig();
                                sender.sendMessage(Main.prefix + name() + " location was set to " + p.getName() + "'s  location.");
                                start();
                            }
                        }
                    }
                    else if (args.length == 7) {
                        if (sender instanceof Player p) {
                            Location loc = new Location(p.getWorld(), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
                            config.getConfig().set("Location", loc);
                            config.saveConfig();
                            sender.sendMessage(Main.prefix + name() + getLocationString(loc));
                            start();
                        } else {
                            sender.sendMessage(Main.prefix + ChatColor.RED + "Error! You must be a player to use this command. You can enter world, x, y, z");
                        }
                    } else if (args.length == 8) {
                        World w = Main.instance.getServer().getWorld(args[4]);
                        if (w == null) {
                            sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid world name.");
                        } else {
                            Location loc = new Location(w, Double.parseDouble(args[5]), Double.parseDouble(args[6]), Double.parseDouble(args[7]));
                            config.getConfig().set("Location", loc);
                            config.saveConfig();
                            sender.sendMessage(Main.prefix + name() + " location was set to " + getLocationString(loc));
                            start();
                        }
                    } else {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid argument!");
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
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 4) {
            if (args[2].equalsIgnoreCase("setting")) {
                return List.of("location", "speed", "radius", "startbelowheight");
            }
        }
        else if (args[3].equalsIgnoreCase("location") && args.length <= 8 && sender instanceof Player p) {
            List<String> worldNames = new ArrayList<>();
            Main.instance.getServer().getWorlds().forEach(w -> worldNames.add(w.getName()));
            if (args.length == 5) {
                ArrayList<String> arguments = new ArrayList<>();
                arguments.add(String.valueOf(Math.floor(p.getLocation().getX()) + 0.5));
                arguments.add("player");
                arguments.addAll(worldNames);
                return arguments;
            } else if (args.length == 6) {
                ArrayList<String> arguments = new ArrayList<>();
                if (args[4].equalsIgnoreCase("player")) {
                    Main.instance.getServer().getOnlinePlayers().forEach(pl -> {
                        if (pl.getName().equalsIgnoreCase(sender.getName())) {
                            arguments.add(0, pl.getName());
                        } else {
                            arguments.add(pl.getName());
                        }
                    });
                } else if (worldNames.contains(args[4])) {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getX()) + 0.5));
                } else {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getY())));
                }
                return arguments;
            } else if (args[4].equalsIgnoreCase("player")) {
                return List.of();
            } else if (args.length == 7) {
                ArrayList<String> arguments = new ArrayList<>();
                if (worldNames.contains(args[4])) {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getY())));
                } else {
                    arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getZ()) + 0.5));
                }
                return arguments;
            } else if (args.length == 8) {
                ArrayList<String> arguments = new ArrayList<>();
                if (worldNames.contains(args[4])) {
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
