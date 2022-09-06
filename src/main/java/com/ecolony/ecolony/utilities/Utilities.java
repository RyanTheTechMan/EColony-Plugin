package com.ecolony.ecolony.utilities;

import com.ecolony.ecolony.Exceptions.CommandSenderType;
import com.ecolony.ecolony.Exceptions.InvalidSenderTypeException;
import com.ecolony.ecolony.Exceptions.PlayerNotFoundException;
import com.ecolony.ecolony.Exceptions.WorldNameException;
import com.ecolony.ecolony.Main;
import org.bukkit.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Utilities {
    @Nullable
    public String getLocationString(@Nullable final Location loc) {
        if (loc == null) return null;
        else return loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ();
    }
    public float clamp(float val, float min, float max) {return Math.max(min, Math.min(max, val));}
    public float clamp(double val, float min, float max) {return Math.max(min, Math.min(max, (float)val));}

    /**
     *
     * @param sender
     * @param args
     * @return a location of specified by the args, or the sender if "player" is arg[0]
     * @throws InvalidSenderTypeException if args[0] is "player" and sender is not a player
     * @throws PlayerNotFoundException if args[0] is "player" and args[1] is not a valid player name
     * @throws WorldNameException if args[0] is an invalid world name
     * @see #getLocationOnCommandReportError(CommandSender, String[]) To avoid throwing exceptions, use this method instead.
     */
    @Nullable
    public Location getLocationOnCommand(@NotNull final CommandSender sender, @NotNull final String[] args) throws InvalidSenderTypeException, PlayerNotFoundException, WorldNameException {
        if (args[0].equalsIgnoreCase("player")) {
            if (args.length == 1) {
                if (sender instanceof Player p) return new Location(p.getWorld(), Math.floor(p.getLocation().getX()) + 0.5, Math.floor(p.getLocation().getY()) + 0.5, Math.floor(p.getLocation().getZ()) + 0.5);
                else throw new InvalidSenderTypeException(sender, CommandSenderType.PLAYER);
            } else if (args.length == 2) {
                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) throw new PlayerNotFoundException(args[1]);
                else return new Location(p.getWorld(), Math.floor(p.getLocation().getX()) + 0.5, Math.floor(p.getLocation().getY()) + 0.5, Math.floor(p.getLocation().getZ()) + 0.5);
            }
        }
        else if (args.length == 3) {
            Location location = null;
            if (sender instanceof Player p) {location = p.getLocation();}
            else if (sender instanceof BlockCommandSender bcs) {location = bcs.getBlock().getLocation();}
            if (location != null) { // Sender is a player or a block command sender
                if (args[0].startsWith("~")) {
                    String s = args[0].substring(1);
                    if (s.isEmpty()) s = "0";
                    args[0] = String.valueOf(location.getX() + Double.parseDouble(s));
                }
                if (args[1].startsWith("~")) {
                    String s = args[1].substring(1);
                    if (s.isEmpty()) s = "0";
                    args[1] = String.valueOf(location.getY() + Double.parseDouble(s));
                }
                if (args[2].startsWith("~")) {
                    String s = args[2].substring(1);
                    if (s.isEmpty()) s = "0";
                    args[2] = String.valueOf(location.getZ() + Double.parseDouble(s));
                }
            }
            if (location != null) new Location(location.getWorld(), Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
            else throw new InvalidSenderTypeException(sender, CommandSenderType.PLAYER);
        } else if (args.length == 4) {
            World w = Main.instance.getServer().getWorld(args[0]);
            Location location = null;
            if (sender instanceof Player p) {location = p.getLocation();}
            else if (sender instanceof BlockCommandSender bcs) {location = bcs.getBlock().getLocation();}
            if (location != null) { // Sender is a player or a block command sender
                if (args[1].startsWith("~")) {
                    String s = args[1].substring(1);
                    if (s.isEmpty()) s = "0";
                    args[1] = String.valueOf(location.getX() + Double.parseDouble(s));
                }
                if (args[2].startsWith("~")) {
                    String s = args[2].substring(1);
                    if (s.isEmpty()) s = "0";
                    args[2] = String.valueOf(location.getY() + Double.parseDouble(s));
                }
                if (args[3].startsWith("~")) {
                    String s = args[3].substring(1);
                    if (s.isEmpty()) s = "0";
                    args[3] = String.valueOf(location.getZ() + Double.parseDouble(s));
                }
            }
            if (w == null) throw new WorldNameException(args[0]);
            else return new Location(w, Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
        }
        return null;
    }

    /**
     *
     * @param sender
     * @param args
     * @return a location of specified by the args, or the sender if "player" is arg[0]
     */
    @Nullable
    public Location getLocationOnCommandReportError(@NotNull final CommandSender sender, @NotNull final String[] args) {
        try {
            return getLocationOnCommand(sender, args);
        } catch (InvalidSenderTypeException e) {
            sender.sendMessage(Main.prefix + ChatColor.RED + "Error! You must be a player to use this command. Use world,x,y,z instead!");
        } catch (WorldNameException e) {
            sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Invalid world name!");
        } catch (PlayerNotFoundException e) {
            sender.sendMessage(Main.prefix + ChatColor.RED + "Error! Player not found!");
        }
        return null;
    }

    public List<String> getLocationTabComplete(CommandSender sender, String[] args) {
        List<String> worldNames = new ArrayList<>();
        Main.instance.getServer().getWorlds().forEach(w -> worldNames.add(w.getName()));
        if (args.length == 1) {
            ArrayList<String> arguments = new ArrayList<>();
            if (sender instanceof Player p) {
                arguments.add(String.valueOf(Math.floor(p.getLocation().getX()) + 0.5));
            }
            arguments.add("player");
            arguments.addAll(worldNames);
            return arguments;
        } else if (args.length == 2) {
            ArrayList<String> arguments = new ArrayList<>();
            if (args[0].equalsIgnoreCase("player")) {
                Main.instance.getServer().getOnlinePlayers().forEach(pl -> {
                    if (pl.getName().equalsIgnoreCase(sender.getName())) {
                        arguments.add(0, pl.getName());
                    } else {
                        arguments.add(pl.getName());
                    }
                });
            } else if (worldNames.contains(args[0])) {
                arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getX()) + 0.5));
            } else {
                arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getY())));
            }
            return arguments;
        } else if (args[0].equalsIgnoreCase("player")) {
            return List.of();
        } else if (args.length == 3) {
            ArrayList<String> arguments = new ArrayList<>();
            if (worldNames.contains(args[0])) {
                arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getY())));
            } else {
                arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getZ()) + 0.5));
            }
            return arguments;
        } else if (args.length == 4) {
            ArrayList<String> arguments = new ArrayList<>();
            if (worldNames.contains(args[0])) {
                arguments.add(String.valueOf(Math.floor(((Player) sender).getLocation().getZ()) + 0.5));
            }
            return arguments;
        }
        return List.of("");
    }

    public Material getRandomItem() { //TODO: add javadoc
        Material mat;
        do {
            mat = Material.values()[new Random().nextInt(Material.values().length)];
        }
        while(!mat.isItem());
        return mat;
    }
}
