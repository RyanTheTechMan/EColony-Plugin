package com.ecolony.ecolony.modules.Generator;

import com.ecolony.ecolony.Main;
import com.ecolony.ecolony.utilities.Particles.Cube;
import com.ecolony.ecolony.utilities.Particles.Vortex;
import com.ecolony.ecolony.utilities.PluginConfig;
import com.ecolony.ecolony.utilities.Utilities;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ItemGenerator extends Utilities {

    private BukkitTask task;

    private final String id;
    private Location location;
    private List<ItemStack> items = new ArrayList<>();
    private int generatorFallTime;
    private String name;
    private boolean enabled;
    private int spawnTime;

    private int lastItemChosen = 0;

    public ItemGenerator(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        saveConfig();
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
        saveConfig();
    }

    public int getFallTime() {
        return generatorFallTime;
    }

    public void setFallTime(int generatorFallTime) {
        this.generatorFallTime = generatorFallTime;
        saveConfig();
    }

    public int getSpawnTime() {
        return spawnTime;
    }

    public void setSpawnTime(int spawnTime) {
        this.spawnTime = spawnTime;
        saveConfig();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        saveConfig();
    }

    public boolean isEnabled(){return enabled;}

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
        saveConfig();
    }

    public void readConfig() {
        PluginConfig config = Main.instance.modules.get("generator").config();
        if (id == null || config.getConfig().get("Generators") == null) {
            new Exception("ItemGenerator id is invalid").printStackTrace();
        }
        else {
            location = config.getConfig().getLocation("Generators." + id + ".Location");
            generatorFallTime = config.getConfig().getInt("Generators." + id + ".FallTime");
            //noinspection unchecked
            items = (List<ItemStack>) config.getConfig().getList("Generators." + id + ".Items", new ArrayList<>());
            name = config.getConfig().getString("Generators." + id + ".Name");
            enabled = config.getConfig().getBoolean("Generators." + id + ".Enabled");
            spawnTime = config.getConfig().getInt("Generators." + id + ".SpawnTime");
        }
    }

    public void saveConfig() {
        PluginConfig config = Main.instance.modules.get("generator").config();
        config.getConfig().set("Generators." + id + ".Location", location);
        config.getConfig().set("Generators." + id + ".FallTime", generatorFallTime);
        config.getConfig().set("Generators." + id + ".Items", items);
        config.getConfig().set("Generators." + id + ".Name", name);
        config.getConfig().set("Generators." + id + ".Enabled", enabled);
        config.getConfig().set("Generators." + id + ".SpawnTime", spawnTime);
        config.saveConfig();
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void start() {
        stop();
        if (isEnabled() && Main.instance.getModule("generator").isEnabled()){
            task = buildTask().runTaskTimer(Main.instance, 0, 20L * spawnTime);
        }
    }

    private BukkitRunnable buildTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (items.isEmpty()) {
                    spawnItem(location, new ItemStack(getRandomItem()));
                }
                else {
                    lastItemChosen = lastItemChosen + (int) (Math.random() * items.size()) % items.size();
                    ItemStack item = items.get(lastItemChosen);
                    spawnItem(location, item);
                }
            }
        };
    }

    private void spawnItem(final Location location, final ItemStack itemStack) {
        final Item e = location.getWorld().dropItem(location, itemStack);
        e.setGravity(false);
        e.setCanMobPickup(false);
        e.setCanPlayerPickup(false);

        final Location lowest = location.clone();
        while(!lowest.getBlock().getType().isSolid() && lowest.getY() >= -64) {
            lowest.subtract(0, 1, 0);
        }
        lowest.add(0,1,0);

        final int taskSpeed = 1; // Higher = slower (ticks per update) TODO: Not working properly. Stay at 1 for now

        BossBar bar = Bukkit.createBossBar("OMG IT'S A " + (itemStack.getItemMeta().displayName() == null ? itemStack.getType().name() : itemStack.getItemMeta().displayName()) + "!!!", BarColor.values()[(int)(Math.random()*BarColor.values().length)], BarStyle.values()[(int)(Math.random()*BarStyle.values().length)]);

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

                e.setVelocity(new Vector(0, -taskSpeed/20d/generatorFallTime*(location.getY() - lowest.getY() - 1), 0));
                bar.setProgress(clamp(itemFallPercent, 0, 1));

                if (part) {
                    e.getLocation().getWorld().spawnParticle(Particle.WAX_ON, e.getLocation().clone().add(Math.sin(itemFallPercent*1000), -1d, Math.cos(itemFallPercent*1000)), 10);
                }
                else {
                    Cube.getParticles(e.getLocation(), 2, itemFallPercent*3000).forEach(loc -> e.getLocation().getWorld().spawnParticle(Particle.COMPOSTER, loc, 1, 0, 0,0, 10));
                }
                double fallPercent = clamp(itemFallPercent, 0f, 1f);
                groundParticles.getParticles(lowest, Particle.REDSTONE, 5, 0.001).forEach(particleBuilder -> particleBuilder.allPlayers().color(Color.fromRGB(255-(int)(255*fallPercent), (int)(255*fallPercent), 0)).spawn());
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
}
