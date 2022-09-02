package com.ecolony.ecolony.utilities.Particles;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.type.Bed;

import java.util.ArrayList;
import java.util.List;

public class CenterWave {
    private int step = 0;
    private int maxStep = 80;
    public boolean reverse = false;
    public boolean pingPong = false; // if true, the vortex will reverse direction after reaching the max step
    private double t = Math.PI/4;
    public List<ParticleBuilder> getParticles(Location location, Particle particle, Particle particle2) {
        List<ParticleBuilder> particles = new ArrayList<>();
        t += reverse ? -0.1*Math.PI : 0.1*Math.PI;
        for (double theta = 0; theta <= 2*Math.PI; theta = theta + Math.PI/32){
            double x = t*Math.cos(theta);
            double y = 2*Math.exp(-0.1*t) * Math.sin(t) + 1.5;
            double z = t*Math.sin(theta);
            particles.add(new ParticleBuilder(particle).location(location.clone().add(x,y,z)));

            theta = theta + Math.PI/64;

            x = t*Math.cos(theta);
            y = 2*Math.exp(-0.1*t) * Math.sin(t) + 1.5;
            z = t*Math.sin(theta);
            particles.add(new ParticleBuilder(particle2).location(location.clone().add(x,y,z)));
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

    /**
     Example usage:
        new BukkitRunnable() {
            CenterWave centerWave = new CenterWave();
            int length = 60;
            @Override
            public void run() {
                centerWave.getParticles(lowest.clone().subtract(0,-1,0), Particle.DRIP_WATER, Particle.DRIP_LAVA).forEach(p -> p.spawn());
                if (length-- <= 0) {
                    cancel();
                }
            }
        }.runTaskTimer(Main.instance, 0, 2);
     */
}
