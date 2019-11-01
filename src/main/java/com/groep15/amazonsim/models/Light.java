package com.groep15.amazonsim.models;

public class Light extends Object3D {
    public Light(World world) {
        super(world);

        this.sy = 8.0 / 32.0;
    }

    @Override
    public boolean update() {
        return false;
    }
}
