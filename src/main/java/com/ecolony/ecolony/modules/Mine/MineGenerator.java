package com.ecolony.ecolony.modules.Mine;

import com.ecolony.ecolony.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class MineGenerator {

    private BukkitTask task;

    private final String id;
    private Location location1;
    private Location location2;
    private String name;
    private boolean enabled;

    private int randomTime;

    /**
     * The minimum amount of time that must pass between spawns.
     */
    private int timeMin;

    /**
     * The maximum amount of time that must pass between spawns.
     */
    private int timeMax;

    public MineGenerator(String id) {
        this.id = id;
    }

    public void start() {
        stop();
        randomTime();
        if (enabled && Main.instance.getModule("mine").isEnabled()) {
            task = buildTask().runTaskTimer(Main.instance, 0, 20);
        }
    }

    public void stop() {
        if (task != null && !task.isCancelled()) task.cancel();
    }

    private void randomTime() {
        randomTime = (int) (Math.random() * (timeMax - timeMin + 1) + timeMin);
    }

    private BukkitRunnable buildTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (randomTime > 0) {
                    randomTime--;
                } else {
                    randomTime();
                    placeBlock(Material.DIAMOND_ORE);
                }
            }
        };
    }

    private void placeBlock(final Material blockType) { // TODO: make an algorithm to place blocks randomly but only if air is present
        int attempts = (location2.getBlockX() - location1.getBlockX()) * (location2.getBlockY() - location1.getBlockY()) * (location2.getBlockZ() - location1.getBlockZ());
        // choose a random location excluding a previously chosen location
        Location location = new Location(location1.getWorld(), (int) (Math.random() * (location2.getBlockX() - location1.getBlockX() + 1) + location1.getBlockX()), (int) (Math.random() * (location2.getBlockY() - location1.getBlockY() + 1) + location1.getBlockY()), (int) (Math.random() * (location2.getBlockZ() - location1.getBlockZ() + 1) + location1.getBlockZ()));
        while (location.getBlock().getType() != Material.AIR && attempts > 0) {
            location = new Location(location1.getWorld(), (int) (Math.random() * (location2.getBlockX() - location1.getBlockX() + 1) + location1.getBlockX()), (int) (Math.random() * (location2.getBlockY() - location1.getBlockY() + 1) + location1.getBlockY()), (int) (Math.random() * (location2.getBlockZ() - location1.getBlockZ() + 1) + location1.getBlockZ()));
            attempts--;
        }
        if (attempts > 0) {
            location.getBlock().setType(blockType);
            location.getWorld().playSound(location, Sound.BLOCK_STONE_PLACE, 1, 1);
            location.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_CRACK, location, 10, 0.5, 0.5, 0.5, 0.1, new ItemStack(blockType));
        }
    }
}
