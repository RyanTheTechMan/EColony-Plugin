package com.ecolony.ecolony.modules;

import com.ecolony.ecolony.Main;
import com.ecolony.ecolony.utilities.Particles.*;
import com.ecolony.ecolony.utilities.PluginConfig;
import com.ecolony.ecolony.utilities.Utilities;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

public class Generator extends Utilities implements Module {

    public Generator() {
        initModule();
    }

    @Override
    public String name() {return "Generator";}

    @Override
    public String description() {return "Spawns items at specified locations based on rarity.";}

    BukkitTask task;
    @Override
    public boolean start() {
        task = buildTask().runTaskTimer(Main.instance, 0, 20*30);
        return true;
    }

    @Override
    public boolean stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        return true;
    }

    @Override
    public PluginConfig config() {
        return null;
    }

    @Override
    public void initConfig() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    private void spawnItem(final Location location, final Material material, final int amount, final double time) {
        final ItemStack is = new ItemStack(material, amount);

        final Item e = location.getWorld().dropItem(location, is);
        e.setGravity(false);
        e.setCanMobPickup(false);
        e.setCanPlayerPickup(false);

        final Location lowest = location.clone();
        while(!lowest.getBlock().getType().isSolid() && lowest.getY() >= -64) {
            lowest.subtract(0, 1, 0);
        }
        lowest.add(0,1,0);

        final int taskSpeed = 1; // Higher = slower (ticks per update) TODO: Not working properly. Stay at 1 for now

        BossBar bar = Bukkit.createBossBar("OMG IT'S A " + material.name() + "!!!", BarColor.values()[(int)(Math.random()*BarColor.values().length)], BarStyle.values()[(int)(Math.random()*BarStyle.values().length)]);

        Main.instance.getServer().getOnlinePlayers().forEach(bar::addPlayer);

        boolean part = Math.random() < 0.5; // TODO: remove this

        Vortex groundParticles = new Vortex();
        groundParticles.pingPong = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!e.isValid() || e.isOnGround()) {
                    e.getLocation().getWorld().spawnParticle(Particle.SONIC_BOOM, e.getLocation(), 1);
                    e.getLocation().getWorld().playSound(e.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
                    e.getLocation().getWorld().playSound(e.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0F, 2.0F);
                    e.setCanPlayerPickup(true);
                    e.setGravity(true);
                    bar.removeAll();
                    cancel();
                    return;
                }

                final double itemFallPercent = (((location.getY()-e.getLocation().getY())/(location.getY() - lowest.getY())));

                e.setVelocity(new Vector(0, -taskSpeed/20d/time*(location.getY() - lowest.getY() - 1), 0));
                bar.setProgress(clamp(itemFallPercent, 0, 1));

                if (part) {
                    e.getLocation().getWorld().spawnParticle(Particle.WAX_ON, e.getLocation().clone().add(Math.sin(itemFallPercent*1000), -1d, Math.cos(itemFallPercent*1000)), 10);
                }
                else {
                    Cube.getParticles(e.getLocation(), 2, itemFallPercent*3000).forEach(loc -> e.getLocation().getWorld().spawnParticle(Particle.COMPOSTER, loc, 1, 0, 0,0, 10));
                }

                groundParticles.getParticles(lowest, Particle.REDSTONE, 5, 0.001).forEach(particleBuilder -> particleBuilder.allPlayers().color(Color.fromRGB(255-(int)(255*itemFallPercent), (int)(255*itemFallPercent), 0)).spawn());
            }
        }.runTaskTimer(Main.instance, 0, taskSpeed);

//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (!e.isValid() || e.isOnGround()) {
//                    cancel();
//                    return;
//                }
//
//                Firework fw = (Firework)e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.FIREWORK);
//                FireworkMeta fwm = fw.getFireworkMeta();
//                FireworkEffect.Type type = FireworkEffect.Type.BALL;
//                FireworkEffect effect = FireworkEffect.builder().flicker(true).withColor(Color.BLUE).withFade(Color.WHITE).with(type).trail(true).build();
//                fwm.addEffect(effect);
//                fwm.setPower(0);
//                fw.setFireworkMeta(fwm);
//                fw.detonate();
//
//
//            }
//        }.runTaskTimer(Main.instance, 0, 5);
    }

    private BukkitRunnable buildTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                // Create and save a location for the generator
                // Spawn an item at the height provided ✓
                // Have item slowly fall to lower height provided ✓
                // When item is x distance from ground, allow item to be picked up
                // Extras:
                // - Use particle effects to show item is falling (based on rarity)

                Location location = new Location(Bukkit.getWorlds().get(0), 0.5, 130, 0.5);
                spawnItem(location, Material.values()[(int)(Math.random()*Material.values().length)], 1, 15);

            }
        };
    }
}
