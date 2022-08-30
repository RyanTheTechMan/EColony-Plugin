package com.ecolony.ecolony.utilities;

import org.bukkit.Location;

public abstract class Utilities {
    public String getLocationString(Location loc) {
        return loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ();
    }
    public float clamp(float val, float min, float max) {return Math.max(min, Math.min(max, val));}
}
