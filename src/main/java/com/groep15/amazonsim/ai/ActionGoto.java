package com.groep15.amazonsim.ai;

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
    private int when;

    public ActionGoto(IWorldActor actor, Vec2i from, Vec2i dest, int when) {
        this.actor = actor;
        this.from = from;
        this.dest = dest;
        this.when = when;
    }

    public ActionGoto(IWorldActor actor, Vec2i dest) {
        this(actor, new Vec2i(actor.getPosition().x, actor.getPosition().z), dest, actor.getWorld().getTickCount());
        this.from = null;
    }

    @Override
    public boolean progress(IWorldActor obj) {
        if (isDone()) return false;

        // If we don't have a path, try and get a new one.
        if (path == null) onWorldChanged();
        if (path == null) return true;

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
    public void clearMovementFuture() {
        this.path = null;
    }

    @Override
    public void onWorldChanged() {
        if (this.isDone()) return;

        Vec2i src = (from == null) ? new Vec2i(actor.getPosition().x, actor.getPosition().z) : from;

        this.path = actor.getWorld().getWorldGraph().calculatePath(actor, src, dest, when);
        if (path != null && actor.getWorld().getWorldGraph().willCollide(actor, src, this.path)) this.path = null;

        if (path != null) {
            for (IWorldActor a : actor.getWorld().getActors()) {
                if (a != actor) a.getAction().clearMovementFuture();
            }
        }
    }
}
