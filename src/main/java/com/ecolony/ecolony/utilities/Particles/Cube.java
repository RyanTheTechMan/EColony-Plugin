package com.ecolony.ecolony.utilities.Particles;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cube {

    public static List<Location> getParticles(final Location location, final double size, final double offset) {
        return getParticles(location, size, offset, new Vector(0.00314159265D, 0.00369599135D, 0.00405366794D));
    }

    public static List<Location> getParticles(final Location location, final double size, final double offset, final Vector angularVelocity) {
        final double particlesPerEdge = 7;

        List<Location> pparticles = new ArrayList<>();
        angularVelocity.multiply(offset);
        double a = size / 2.0D;
        Vector v = new Vector();

        for (int i = 0; i < 4; ++i) {
            double angleY = (double) i * Math.PI / 2.0D;

            int p;
            for (p = 0; p < 2; ++p) {
                double angleX = (double) p * Math.PI;

                for (int p2 = 0; p2 <= particlesPerEdge; ++p2) {
                    v.setX(a).setY(a);
                    v.setZ(size * p2 / particlesPerEdge - a);
                    VectorUtils.rotateAroundAxisX(v, angleX);
                    VectorUtils.rotateAroundAxisY(v, angleY);
                    VectorUtils.rotateVector(v, angularVelocity.getX(), angularVelocity.getY(), angularVelocity.getZ());
                    pparticles.add(location.clone().add(v));
                }
            }

            for (p = 0; p <= particlesPerEdge; ++p) {
                v.setX(a).setZ(a);
                v.setY(size * p / particlesPerEdge - a);
                VectorUtils.rotateAroundAxisY(v, angleY);
                VectorUtils.rotateVector(v, angularVelocity.getX(), angularVelocity.getY(), angularVelocity.getZ());
                pparticles.add(location.clone().add(v));
            }
        }
        return pparticles;
    }
}