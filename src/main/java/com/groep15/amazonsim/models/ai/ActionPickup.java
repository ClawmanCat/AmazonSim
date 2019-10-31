package com.groep15.amazonsim.models.ai;

import com.groep15.amazonsim.models.Object3D;
import com.groep15.amazonsim.utility.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionPickup implements IWorldAction {
    private Object3D target;

    public ActionPickup(Object3D target) {
        this.target = target;
    }

    @Override
    public boolean progress(IWorldActor obj) {
        if (isDone()) return false;

        obj.grab(target);
        this.target = null;

        return true;
    }

    @Override
    public boolean isDone() {
        return target == null;
    }

    @Override
    public List<Direction> getMovementFuture() {
        return this.isDone() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(Direction.NONE));
    }

    @Override
    public void onWorldChanged() { }
}
