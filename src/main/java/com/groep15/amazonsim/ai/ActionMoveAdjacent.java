package com.groep15.amazonsim.ai;

import com.groep15.amazonsim.utility.Direction;
import com.groep15.amazonsim.utility.Utility;
import com.sun.javafx.geom.Vec3d;

public class ActionMoveAdjacent implements IWorldAction {
    private Vec3d old;
    private Direction dir;
    private boolean done;

    public ActionMoveAdjacent(Direction dir) {
        this.dir = dir;
        this.done = false;
    }

    @Override
    public boolean progress(IWorldActor obj) {
        if (done) return false;

        Vec3d pos = obj.getPosition();
        if (Utility.approx(pos.x, old.x + dir.movement.x) && Utility.approx(pos.z, old.z + dir.movement.y)) {
            done = true;
        } else {
            pos.x += Utility.absMin(dir.movement.x * obj.getSpeed(), (old.x + dir.movement.x) - pos.x);
            pos.z += Utility.absMin(dir.movement.y * obj.getSpeed(), (old.z + dir.movement.y) - pos.z);

            obj.setPosition(pos.x, pos.y, pos.z);
        }

        return true;
    }

    @Override
    public boolean onActionStart(IWorldActor obj) {
        this.old = obj.getPosition();
        this.done = false;
        return false;
    }

    @Override
    public boolean onActionDone(IWorldActor obj) {
        return false;
    }

    public boolean hasArrived() {
        return done;
    }
}
