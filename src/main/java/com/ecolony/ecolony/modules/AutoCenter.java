package com.ecolony.ecolony.modules;

import com.ecolony.ecolony.Main;
import com.ecolony.ecolony.utilities.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class AutoCenter implements Module {

    // MODULE SETUP - START
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
        config.saveConfig();
    }

    // MODULE SETUP - END

    private Runnable task;
    private int taskID;
    public AutoCenter() {
        initModule();

        final Location centerPosition = config.getConfig().getLocation("Location");

        task = new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().getOnlinePlayers().forEach(p -> {
                    Vector velocity = p.getVelocity();
                    Location loc = p.getLocation();
                    if (velocity.getY() < -1 && (Math.abs(loc.getX()) > 8 || Math.abs(loc.getZ()) > 8) & (loc.getY() == -0d || loc.getY() < centerPosition.getY())){
                        velocity.setX(velocity.getX()+0.03*-clamp((float)(loc.getX() + centerPosition.getX()), -3, 3));
                        velocity.setZ(velocity.getZ()+0.03*-clamp((float) (loc.getZ() + centerPosition.getZ()), -3, 3));
                        p.setVelocity(velocity);
                    }
                });
            }
        };

        this.start();
    }

    @Override
    public boolean start() {
        if (!Main.instance.scheduler.isCurrentlyRunning(taskID)) {
            taskID = Main.instance.scheduler.scheduleSyncRepeatingTask(Main.instance, task, 0, 1);
            return taskID != -1;
        }
        return false;
    }

    @Override
    public boolean stop() {
        if (Main.instance.scheduler.isCurrentlyRunning(taskID)) {
            Main.instance.scheduler.cancelTask(taskID);
            return true;
        }
        return false;
    }

    public static float clamp(float val, float min, float max) {return Math.max(min, Math.min(max, val));}
}
