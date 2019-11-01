package com.groep15.amazonsim.models;

import com.groep15.amazonsim.models.worldobject.Object3D;

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
