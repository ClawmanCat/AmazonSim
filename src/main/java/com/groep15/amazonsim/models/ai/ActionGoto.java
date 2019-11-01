package com.groep15.amazonsim.models.ai;

import com.groep15.amazonsim.utility.Direction;
import com.groep15.amazonsim.utility.Vec2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionGoto implements IWorldAction {
    private Vec2i from, dest;
    private List<Direction> path;
    private ActionMove mover;
    private IWorldActor actor;

    public ActionGoto(IWorldActor actor, Vec2i from, Vec2i dest) {
        this.actor = actor;
        this.from = from;
        this.dest = dest;
    }

    @Override
    public boolean progress(IWorldActor obj) {
        if (isDone()) return false;

        // If we don't have a path, try and get a new one.
        if (path == null) onWorldChanged(new ArrayList<>());
        if (path == null) return true;

        if (path.size() == 0 && (mover == null || mover.isDone())) return true;

        if (mover == null || mover.isDone()) mover = new ActionMove(path.remove(0));
        return mover.progress(obj);
    }

    @Override
    public boolean isDone() {
        return path != null && path.size() == 0 && (mover == null || mover.isDone());
    }

    @Override
    public List<Direction> getMovementFuture() {
        return path == null ? new ArrayList<>(Arrays.asList(Direction.NONE)) : path;
    }

    @Override
    public void onWorldChanged(List<IWorldActor> doNotDisturb) {
        if (this.isDone() || doNotDisturb.contains(actor)) return;

        Vec2i src = new Vec2i(actor.getPosition().x, actor.getPosition().z);

        // Try to get a path that doesn't overlap anyone else's path.
        //this.path = actor.getWorld().getWorldGraph().calculatePath(actor, src, dest, true);
        //if (path != null) return;

        // If no such path exists, take one that overlaps another's path, and wait until its clear.
        this.path = actor.getWorld().getWorldGraph().calculatePath(actor, src, dest);

        if (actor.getWorld().getWorldGraph().collides(this.actor, src, this.path)) {
            this.path = null;   // Can't move yet, just wait.
        }
    }
}
