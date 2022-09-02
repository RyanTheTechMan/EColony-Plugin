package com.ecolony.ecolony.utilities.Particles;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;

public class Whirlwind {

    private double step = 0;
    private int maxStep = 40;
    public boolean reverse = false;

    public List<ParticleBuilder> getParticles(Location location, Particle particle, double speed, int rays) {
        List<ParticleBuilder> particles = new ArrayList<>();

        for(int i = 0; i < rays; ++i) {
            double dx = MathL.cos(this.step + Math.PI * 2 * ((double)i / rays));
            double dz = MathL.sin(this.step + Math.PI * 2 * ((double)i / rays));
            double angle = Math.atan2(dz, dx);
            double xAng = MathL.cos(angle);
            double zAng = MathL.sin(angle);
            particles.add(new ParticleBuilder(particle).location(location.clone()).offset(xAng, 0d,  zAng).extra(speed));
        }
        if (reverse) {
            step-=(Math.PI * 2);
            if(step <= maxStep) step = maxStep;
        } else {
            step+=(Math.PI * 2);
            if(step >= maxStep) step = maxStep;
        }



        return particles;
    }
}
