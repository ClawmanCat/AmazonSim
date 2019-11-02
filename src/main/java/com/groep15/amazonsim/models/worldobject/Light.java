package com.groep15.amazonsim.models.worldobject;

import com.groep15.amazonsim.models.World;

public class Light extends Object3D {
    public Light(World world) {
        super(world);

        this.sy = 8.0 / 32.0;
        this.passable = true;
    }

    @Override
    public boolean update() {
        return false;
    }
}
