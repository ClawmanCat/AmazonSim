package com.groep15.amazonsim.utility;

public enum Direction {
    NONE(new Vec2i(0, 0)), FORWARDS(new Vec2i(0, 1)), BACKWARDS(new Vec2i(0, -1)), RIGHT(new Vec2i(1, 0)), LEFT(new Vec2i(-1, 0));

    public final Vec2i movement;

    private Direction(Vec2i movement) {
        this.movement = movement;
    }

    public Direction invert() {
        switch (this) {
            case FORWARDS:  return BACKWARDS;
            case BACKWARDS: return FORWARDS;
            case LEFT:      return RIGHT;
            case RIGHT:     return LEFT;
            default:        return NONE;
        }
    }
}
