package com.groep15.amazonsim.utility;

public final class Utility {
    private Utility() {}

    public static boolean approx(double a, double b) {
        final double epsilon = 0.00001;
        return Math.abs(a - b) <= epsilon;
    }

    public static double absMin(double a, double b) {
        return Math.abs(a) < Math.abs(b) ? a : b;
    }
}
