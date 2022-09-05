package com.ecolony.ecolony.modules.Mine;

import com.ecolony.ecolony.Main;
import com.ecolony.ecolony.utilities.Module;
import com.ecolony.ecolony.utilities.PluginConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Mine implements Module, Listener {
    private PluginConfig config;

    @Override
    public @NotNull String name() {
        return "Mine";
    }

    public Mine() {initModule();}

    private final ArrayList<Player> inSetupMode = new ArrayList<>();

    @Override
    public @NotNull String description() {
        return "Spawns blocks at specified locations.";
    }
    @Override
    public boolean start() {
        final BukkitRunnable runnable = buildTask();
        return true;
    }

    @Override
    public boolean stop() {

        return true;
    }

    @Override
    public PluginConfig config() {
        return config;
    }

    @Override
    public void initConfig() {
        config = generateConfig();
        config.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    private BukkitRunnable buildTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = new Location(Main.instance.getServer().getWorld("world"), 0, 100, 0);
                loc.getBlock().setType(Material.AMETHYST_BLOCK);
            }
        };
    }

    public void onBlockBreak(BlockBreakEvent event) {

    }

    public void onCommandEvent(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/cancel") && inSetupMode.contains(event.getPlayer())) {
            event.setCancelled(true);
            if (isEnabled()) {
                stop();
                event.getPlayer().sendMessage("Mine module disabled.");
            } else {
                start();
                event.getPlayer().sendMessage("Mine module enabled.");
            }
        }
    }
}
