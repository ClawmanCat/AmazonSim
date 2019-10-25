package com.groep15.amazonsim.utility;

// JavaFX is not supported for some people, we can use our own vector class instead.
public class Vec3d {
    public double x, y, z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d(Vec3d other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }
}
