package com.groep15.amazonsim.models;

public class Robot extends Object3D {
    public Robot(World world) {
        super(world);
    }

    @Override
    public boolean update() {
        int tick = world.getTickCount();

        // Move in circles.
        if (tick % 32 < 8) x += 0.5;
        else if (tick % 32 < 16) z += 0.5;
        else if (tick % 32 < 24) x -= 0.5;
        else z -= 0.5;

        return true;
    }
}