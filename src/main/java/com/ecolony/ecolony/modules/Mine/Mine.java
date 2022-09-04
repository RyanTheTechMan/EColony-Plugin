package com.ecolony.ecolony.modules.Mine;

import com.ecolony.ecolony.Main;
import com.ecolony.ecolony.utilities.Module;
import com.ecolony.ecolony.utilities.PluginConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Mine implements Module {
    private PluginConfig config;

    @Override
    public @NotNull String name() {
        return "Mine";
    }

    public Mine() {initModule();}

    @Override
    public @NotNull String description() {
        return "Spawns blocks at specified locations.";
    }
    private BukkitTask task;
    @Override
    public boolean start() {
        if (task != null && !task.isCancelled()) task.cancel();
        final BukkitRunnable runnable = buildTask();

        task = runnable.runTaskTimer(Main.instance, 0, 60);
        return true;
    }

    @Override
    public boolean stop() {
        if (task != null && !task.isCancelled()) task.cancel();
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
}
