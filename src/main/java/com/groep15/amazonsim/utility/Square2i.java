package com.groep15.amazonsim.utility;

import java.util.Random;

public class Square2i {
    public Vec2i a, b;

    public Square2i(Vec2i a, Vec2i b) {
        this.a = a;
        this.b = b;
    }

    public Square2i(Square2i other) {
        this.a = other.a;
        this.b = other.b;
    }

    public Vec2i randomPointWithin() {
        Random r = new Random();

        return new Vec2i(
                r.nextInt(Math.max(a.x, b.x)) + Math.min(a.x, b.x),
                r.nextInt(Math.max(a.y, b.y)) + Math.min(a.y, b.y)
        );
    }

    public Vec2i size() {
        return new Vec2i(b.x - a.x, b.y - a.y);
    }
}
