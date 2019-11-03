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
    private int moveCount;
    private int dx, dz;
    private int pauseFor;

    public ActionGoto(IWorldActor actor, Vec2i from, Vec2i dest) {
        this.actor = actor;
        this.from = from;
        this.dest = dest;

        this.moveCount = 1;
        this.dx = this.dz = 0;
        this.pauseFor = 0;
    }

    @Override
    public boolean progress(IWorldActor obj) {
        if (isDone()) return false;

        if (this.pauseFor > 0) {
            --this.pauseFor;
            return true;
        }

        onWorldChanged(new ArrayList<>());
        if (path == null) return true;

        // Robots might occasionally get deadlocked with each other. Usually another robot will pass by close enough
        // at some point to resolve it.
        // Just in case, keep track of the average speed, and pause the robot for a bit if it drops too low.
        // This isn't very aggressive, so the robots might remain in deadlock for a while before detection occurs.
        if (path.size() > 0) {
            this.dx += path.get(0).movement.x;
            this.dz += path.get(0).movement.y;

            if (moveCount > 100) {
                double distance = Math.sqrt(dx * dx + dz * dz);
                double avgspeed = distance / (moveCount * moveCount);

                if (avgspeed < 1E-4) {
                    System.out.println("Detected deadlock. Pausing robot movement.");
                    this.pauseFor  = 180;
                    this.moveCount = 0;
                }
            }
        }

        if (path.size() == 0 && (mover == null || mover.isDone())) return true;

        if (mover == null || mover.isDone()) mover = new ActionMove(path.remove(0));

        ++this.moveCount;
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
