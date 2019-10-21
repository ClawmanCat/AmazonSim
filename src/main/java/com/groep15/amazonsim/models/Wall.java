package com.groep15.amazonsim.models;

public class Wall extends Object3D {
    public Wall(World world) {
        super(world);
    }

    @Override
    public boolean update() {
        return false;
    }
}
