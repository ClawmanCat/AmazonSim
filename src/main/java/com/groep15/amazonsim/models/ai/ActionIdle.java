package com.groep15.amazonsim.models.ai;

import com.groep15.amazonsim.utility.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionIdle implements IWorldAction {
    private boolean done = false;

    @Override
    public boolean progress(IWorldActor obj) {
        if (this.isDone()) return false;

        obj.setPosition(Math.round(obj.getPosition().x), obj.getPosition().y, Math.round(obj.getPosition().z));
        this.done = true;

        return true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public List<Direction> getMovementFuture() {
        return this.isDone() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(Direction.NONE));
    }

    @Override
    public void onWorldChanged() { }
}
