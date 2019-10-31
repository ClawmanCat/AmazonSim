package com.groep15.amazonsim.models.ai;

import com.groep15.amazonsim.utility.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionCompound implements IWorldAction {
    private List<IWorldAction> actions;
    private IWorldAction current;

    public ActionCompound(IWorldAction... actions) {
        this.actions = new ArrayList<>(Arrays.asList(actions));
    }

    @Override
    public boolean progress(IWorldActor obj) {
        if (isDone()) return false;

        if (current == null || current.isDone()) current = actions.remove(0);

        return current.progress(obj);
    }

    @Override
    public boolean isDone() {
        return actions.size() == 0 && (current == null || current.isDone());
    }

    @Override
    public List<Direction> getMovementFuture() {
        List<IWorldAction> a = new ArrayList<>(this.actions);
        if (current != null) a.add(0, current);

        return a.stream()
                .map(IWorldAction::getMovementFuture)
                .reduce(new ArrayList<>(), (tmp, elem) -> { if (elem != null) tmp.addAll(elem); return tmp; });
    }

    @Override
    public void onWorldChanged(List<IWorldActor> doNotDisturb) {
        for (IWorldAction action : actions) action.onWorldChanged(doNotDisturb);
    }
}
