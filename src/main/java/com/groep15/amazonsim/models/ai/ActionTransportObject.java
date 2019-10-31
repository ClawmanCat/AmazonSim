package com.groep15.amazonsim.models.ai;

import com.groep15.amazonsim.models.worldobject.Object3D;
import com.groep15.amazonsim.utility.Direction;
import com.groep15.amazonsim.utility.Vec2i;

import java.util.List;

public class ActionTransportObject implements IWorldAction {
    private Object3D target;
    private Vec2i dest;
    private ActionCompound actions;

    public ActionTransportObject(IWorldActor actor, Object3D target, Vec2i apos, Vec2i spos, Vec2i dest) {
        this.target = target;
        this.dest = dest;

        this.actions = new ActionCompound(
                new ActionGoto(actor, apos, spos),
                new ActionPickup(target),
                new ActionGoto(actor, spos, dest),
                new ActionRelease()
        );
    }

    public ActionTransportObject(IWorldActor actor, Object3D target, Vec2i apos, Vec2i dest) {
        this(actor, target, apos, new Vec2i(target.getPosition().x, target.getPosition().z), dest);
    }

    @Override
    public boolean progress(IWorldActor obj) {
        return this.actions.progress(obj);
    }

    @Override
    public boolean isDone() {
        return this.actions.isDone();
    }

    @Override
    public List<Direction> getMovementFuture() {
        return this.actions.getMovementFuture();
    }

    @Override
    public void onWorldChanged(List<IWorldActor> doNotDisturb) {
        this.actions.onWorldChanged(doNotDisturb);
    }
}
