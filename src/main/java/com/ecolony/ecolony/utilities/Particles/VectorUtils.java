package com.ecolony.ecolony.utilities.Particles;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public final class VectorUtils {
    private VectorUtils() {
    }

    public static Vector rotateAroundAxisX(Vector v, double angle) {
        double cos = MathL.cos(angle);
        double sin = MathL.sin(angle);
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double angle) {
        double cos = MathL.cos(angle);
        double sin = MathL.sin(angle);
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double angle) {
        double cos = MathL.cos(angle);
        double sin = MathL.sin(angle);
        double x = v.getX() * cos - v.getY() * sin;
        double y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    public static Vector rotateVector(Vector v, double angleX, double angleY, double angleZ) {
        rotateAroundAxisX(v, angleX);
        rotateAroundAxisY(v, angleY);
        rotateAroundAxisZ(v, angleZ);
        return v;
    }

    public static Vector rotateVector(Vector v, Location location) {
        return rotateVector(v, location.getYaw(), location.getPitch());
    }

    public static Vector rotateVector(Vector v, float yawDegrees, float pitchDegrees) {
        double yaw = Math.toRadians((double)(-1.0F * (yawDegrees + 90.0F)));
        double pitch = Math.toRadians((double)(-pitchDegrees));
        double cosYaw = MathL.cos(yaw);
        double cosPitch = MathL.cos(pitch);
        double sinYaw = MathL.sin(yaw);
        double sinPitch = MathL.sin(pitch);
        double initialX = v.getX();
        double initialY = v.getY();
        double x = initialX * cosPitch - initialY * sinPitch;
        double y = initialX * sinPitch + initialY * cosPitch;
        double initialZ = v.getZ();
        double z = initialZ * cosYaw - x * sinYaw;
        x = initialZ * sinYaw + x * cosYaw;
        return new Vector(x, y, z);
    }

    public static double angleToXAxis(Vector vector) {
        return Math.atan2(vector.getX(), vector.getY());
    }
}
