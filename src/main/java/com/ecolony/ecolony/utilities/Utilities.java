package com.ecolony.ecolony.utilities;

import com.ecolony.ecolony.Exceptions.CommandSenderType;
import com.ecolony.ecolony.Exceptions.InvalidSenderTypeException;
import com.ecolony.ecolony.Exceptions.PlayerNotFoundException;
import com.ecolony.ecolony.Exceptions.WorldNameException;
import com.ecolony.ecolony.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

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
            if (sender instanceof Player p) return new Location(p.getWorld(), Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
            else throw new InvalidSenderTypeException(sender, CommandSenderType.PLAYER);
        } else if (args.length == 4) {
            World w = Main.instance.getServer().getWorld(args[2]);
            if (w == null) throw new WorldNameException(args[2]);
            else return new Location(w, Double.parseDouble(args[3]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
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
}
