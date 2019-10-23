package com.groep15.amazonsim.ai;

import com.groep15.amazonsim.utility.Direction;
import com.groep15.amazonsim.utility.Vec2i;

import java.util.ArrayList;
import java.util.List;

public class ActionGoto implements IWorldAction {
    private Vec2i src;
    private Vec2i dest;
    private List<Direction> path;
    private int where;
    private ActionMoveAdjacent move;

    public ActionGoto(Vec2i dest) {
        this.dest = dest;
        this.path = new ArrayList<>();
        this.where = 0;
        this.move = null;
    }

    @Override
    public boolean progress(IWorldActor obj) {
        if (where == this.path.size() && this.move != null && this.move.hasArrived()) {
            obj.setPosition(src.x, obj.getPosition().y, src.y);
            where = 0;
        }

        if (this.move == null || this.move.hasArrived()) {
            if (this.move != null) this.move.onActionDone(obj);
            this.move = new ActionMoveAdjacent(path.get(where));
            this.move.onActionStart(obj);

            ++where;
        }

        this.move.progress(obj);
        return true;
    }

    @Override
    public boolean onActionStart(IWorldActor obj) {
        Vec2i pos = new Vec2i(obj.getPosition().x, obj.getPosition().z);
        this.src = pos;

        this.path = obj.getWorld().getWorldGraph().calculatePath(pos, dest);
        this.where = 0;

        return false;
    }

    @Override
    public boolean onActionDone(IWorldActor obj) {
        return false;
    }

    public boolean hasArrived() {
        return where == this.path.size();
    }
}
