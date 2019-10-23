package com.groep15.amazonsim.utility;

import java.util.Objects;

public class Vec2i {
    public int x, y;

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2i(double x, double y) {
        this.x = (int) Math.floor(x);
        this.y = (int) Math.floor(y);
    }


    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vec2i)) return false;

        Vec2i vec2i = (Vec2i) o;
        return x == vec2i.x &&
               y == vec2i.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
