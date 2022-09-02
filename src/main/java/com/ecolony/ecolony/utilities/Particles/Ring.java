package com.ecolony.ecolony.utilities.Particles;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;

public class Ring {
    public static List<ParticleBuilder> getParticles(Location location, final Particle particle, final double size, final double speed) {
        List<ParticleBuilder> particles = new ArrayList<>();
        int points = 50;
        for (int i = 0; i < points; ++i) {
            double dx = MathL.cos(Math.PI * 2 * ((double) i / (double) points)) * size;
            double dz = MathL.sin(Math.PI * 2 * ((double) i / (double) points)) * size;
            double angle = Math.atan2(dz, dx);
            double xAng = MathL.cos(angle);
            double zAng = MathL.sin(angle);
            particles.add(new ParticleBuilder(particle).location(location.clone().add(dx, 0, dz)).offset(xAng, 0.0D, zAng).extra(speed).force(true));
        }
        return particles;
    }
}
