package com.groep15.amazonsim.models.ai;

import com.groep15.amazonsim.utility.Direction;
import com.groep15.amazonsim.utility.Utility;
import com.groep15.amazonsim.utility.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionMove implements IWorldAction {
    private Direction direction;
    private int count;
    private boolean done;

    public ActionMove(Direction direction) {
        this.direction = direction;
        this.count = 0;
        this.done = false;
    }

    @Override
    public boolean progress(IWorldActor obj) {
        if (this.isDone()) return false;

        // (1 / speed) should be an integer.
        assert(Utility.Approx(1.0 / obj.getSpeed(), Math.round(1.0 / obj.getSpeed())));

        int limit = (int) Math.round(1.0 / obj.getSpeed());
        Vec3d  pos = obj.getPosition();
        double spd = obj.getSpeed();

        if (count < limit) {
            obj.setPosition(pos.x + (direction.movement.x * spd), pos.y, pos.z + (direction.movement.y * spd));

            ++count;
        } else {
            obj.setPosition(Math.round(pos.x), pos.y, Math.round(pos.z));
            done = true;
        }

        return true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public List<Direction> getMovementFuture() {
        return done ? new ArrayList<>() : Arrays.asList(direction);
    }

    @Override
    public void onWorldChanged(List<IWorldActor> doNotDisturb) { }
}
