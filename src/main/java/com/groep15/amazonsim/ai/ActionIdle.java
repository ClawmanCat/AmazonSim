package com.groep15.amazonsim.ai;

import com.sun.javafx.geom.Vec3d;

public class ActionIdle implements IWorldAction {
    private boolean idle = false;

    @Override
    public boolean progress(IWorldActor obj) {
        if (idle) return false;

        Vec3d  pos = obj.getPosition();
        
        // Move to the center of the tile if we're not already.
        double dx = pos.x - Math.floor(pos.x);
        double dy = pos.y - Math.floor(pos.y);
        double dz = pos.z - Math.floor(pos.z);

        obj.setPosition(pos.x + dx, pos.y + dy, pos.z + dz);

        idle = true;
        return true;
    }

    @Override
    public boolean onActionStart(IWorldActor obj) {
        idle = false;
        return false;
    }

    @Override
    public boolean onActionDone(IWorldActor obj) {
        return false;
    }
}
