package com.ecolony.ecolony.utilities.Particles;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Vortex {
    private int step = 0;
    private int maxStep = 70;
    public boolean reverse = false;
    public boolean pingPong = false; // if true, the vortex will reverse direction after reaching the max step

    public List<ParticleBuilder> getParticles(Location location, Particle particle, double size, double speed) {
        return getParticles(location, particle, size, speed, 8, 0.05, 16);
    }

    public List<ParticleBuilder> getParticles(Location location, Particle particle, double size, double speed, int helices, double grow, double radials) {
        List<ParticleBuilder> particles = new ArrayList<>();
        final double radius = size * (1.0D - (double)this.step / (double)maxStep);

        for(int i = 0; i < helices; ++i) {
            double angle = (double)this.step * radials + Math.PI * 2 * (double)i / (double)helices;
            Vector v = new Vector(MathL.cos(angle) * radius, (double)this.step * grow - 1.0D, MathL.sin(angle) * radius);
            particles.add(new ParticleBuilder(particle).location(location.clone().add(v)).extra(speed));
        }

        if (pingPong && (this.step == maxStep || this.step == -1)) {
            this.reverse = !this.reverse;
        }

        if (reverse) {
            step--;
        } else {
            step++;
        }


        return particles;
    }
}
