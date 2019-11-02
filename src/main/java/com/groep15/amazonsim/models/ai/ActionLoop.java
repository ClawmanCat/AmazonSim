package com.groep15.amazonsim.models.ai;

import com.groep15.amazonsim.utility.Direction;

import java.util.List;
import java.util.function.Supplier;

public class ActionLoop implements IWorldAction {
    private IWorldAction action;
    private Supplier<IWorldAction> supplier;
    private Supplier<Boolean> exit;
    private boolean done;

    public ActionLoop(Supplier<IWorldAction> supplier, Supplier<Boolean> exit) {
        this.supplier = supplier;
        this.exit = exit;
        this.done = false;

        this.action = supplier.get();
    }

    public ActionLoop(Supplier<IWorldAction> supplier) {
        this(supplier, () -> false);
    }

    @Override
    public boolean progress(IWorldActor obj) {
        if (isDone()) return false;

        if (this.action.isDone()) {
            this.action = supplier.get();
        }

        boolean result = this.action.progress(obj);
        if (exit.get()) done = true;
        return result;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public List<Direction> getMovementFuture() {
        return this.action.getMovementFuture();
    }

    @Override
    public void onWorldChanged(List<IWorldActor> doNotDisturb) {
        this.action.onWorldChanged(doNotDisturb);
    }
}
