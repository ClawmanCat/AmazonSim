package com.groep15.amazonsim.models;

public class Wall extends Object3D {
    public Wall(World world) {
        super(world);

        this.sy = 2.0;
    }

    @Override
    public boolean update() {
        return false;
    }
}
