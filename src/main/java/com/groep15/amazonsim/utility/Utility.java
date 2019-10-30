package com.groep15.amazonsim.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Utility {
    private Utility() {}

    public static boolean Approx(double a, double b) {
        final double epsilon = 0.00001;
        return Math.abs(a - b) <= epsilon;
    }

    public static double AbsMin(double a, double b) {
        return Math.abs(a) < Math.abs(b) ? a : b;
    }

    public static List<Vec2i> DirectionsToPositions(List<Direction> directions, Vec2i start) {
        List<Vec2i> result = new ArrayList<>();

        result.add(start);
        for (Direction d : directions) {
            Vec2i last = result.get(result.size() - 1);
            result.add(new Vec2i(last.x + d.movement.x, last.y + d.movement.y));
        }

        return result;
    }

    public static <T> boolean Contains(List<T> list, Predicate<T> checker) {
        for (T elem : list) if (checker.test(elem)) return true;
        return false;
    }
}
