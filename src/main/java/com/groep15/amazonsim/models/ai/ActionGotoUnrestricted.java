package com.groep15.amazonsim.models.ai;

import com.groep15.amazonsim.utility.Direction;
import com.groep15.amazonsim.utility.Vec2i;

import java.util.ArrayList;
import java.util.List;

public class ActionGotoUnrestricted implements IWorldAction {
    private Vec2i from, dest;
    private List<Direction> path;
    private ActionMove mover;

    public ActionGotoUnrestricted(Vec2i from, Vec2i dest) {
        this.from = from;
        this.dest = dest;
        this.path = new ArrayList<>();

        Vec2i current = from;
        while (!current.equals(dest)) {
            Direction next = Direction.NONE;
            if (current.x > dest.x) next = Direction.LEFT;
            if (current.y > dest.y) next = Direction.BACKWARDS;
            if (current.x < dest.x) next = Direction.RIGHT;
            if (current.y < dest.y) next = Direction.FORWARDS;

            this.path.add(next);
            current = new Vec2i(current.x + next.movement.x, current.y + next.movement.y);
        }
    }

    @Override
    public boolean progress(IWorldActor obj) {
        if (isDone()) return false;

        if (path.size() == 0 && (mover == null || mover.isDone())) return true;
        if (mover == null || mover.isDone()) mover = new ActionMove(path.remove(0));

        return mover.progress(obj);
    }

    @Override
    public boolean isDone() {
        return path.size() == 0 && (mover == null || mover.isDone());
    }

    @Override
    public List<Direction> getMovementFuture() {
        return path;
    }

    @Override
    public void onWorldChanged(List<IWorldActor> doNotDisturb) { }
}
