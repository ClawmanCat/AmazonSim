package com.groep15.amazonsim.models.ai;

import com.groep15.amazonsim.utility.Direction;
import com.groep15.amazonsim.utility.Vec2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionGoto implements IWorldAction {
    static double lowest = 100;

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

        onWorldChanged(new ArrayList<>());
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
        this.path = actor.getWorld().getWorldGraph().calculatePath(actor, src, dest);
    }
}
