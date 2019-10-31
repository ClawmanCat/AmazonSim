package com.groep15.amazonsim.models.ai;

import com.groep15.amazonsim.models.Object3D;
import com.groep15.amazonsim.utility.Direction;
import com.groep15.amazonsim.utility.Vec2i;

import java.util.List;

public class ActionTransportObject implements IWorldAction {
    private Object3D target;
    private Vec2i dest;
    private ActionCompound actions;

    public ActionTransportObject(IWorldActor actor, Object3D target, Vec2i src, Vec2i dest) {
        this.target = target;
        this.dest = dest;

        Vec2i obpos = new Vec2i(target.getPosition().x, target.getPosition().z);

        this.actions = new ActionCompound(
                new ActionGoto(actor, src, obpos),
                new ActionPickup(target),
                new ActionGoto(actor, obpos, dest),
                new ActionRelease()
        );
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
    public void onWorldChanged() {
        this.actions.onWorldChanged();
    }
}
