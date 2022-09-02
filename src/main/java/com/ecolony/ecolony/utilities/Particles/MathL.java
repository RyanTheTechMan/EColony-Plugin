package com.ecolony.ecolony.utilities.Particles;

public final class MathL {
    private static final int SIN_BITS = 12;
    private static final int SIN_MASK;
    private static final int SIN_COUNT;
    private static final double radFull;
    private static final double radToIndex;
    private static final double degFull;
    private static final double degToIndex;
    private static final double[] sin;
    private static final double[] cos;

    public static double sin(double rad) {
        return sin[(int)(rad * radToIndex) & SIN_MASK];
    }

    public static double cos(double rad) {
        return cos[(int)(rad * radToIndex) & SIN_MASK];
    }

    static {
        SIN_MASK = ~(-1 << SIN_BITS);
        SIN_COUNT = SIN_MASK + 1;
        radFull = Math.PI * 2;
        degFull = 360.0D;
        radToIndex = (double)SIN_COUNT / radFull;
        degToIndex = (double)SIN_COUNT / degFull;
        sin = new double[SIN_COUNT];
        cos = new double[SIN_COUNT];

        int i;
        for(i = 0; i < SIN_COUNT; ++i) {
            sin[i] = Math.sin((double)(((float)i + 0.5F) / (float)SIN_COUNT) * radFull);
            cos[i] = Math.cos((double)(((float)i + 0.5F) / (float)SIN_COUNT) * radFull);
        }

        for(i = 0; i < 360; i += 90) {
            sin[(int)((double)i * degToIndex) & SIN_MASK] = Math.sin((double)i * Math.PI / 180.0D);
            cos[(int)((double)i * degToIndex) & SIN_MASK] = Math.cos((double)i * Math.PI / 180.0D);
        }

    }
}
