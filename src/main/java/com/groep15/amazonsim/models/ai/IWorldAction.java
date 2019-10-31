package com.groep15.amazonsim.models.ai;

import com.groep15.amazonsim.utility.Direction;

import java.util.List;

public interface IWorldAction {
    boolean progress(IWorldActor obj);
    boolean isDone();

    List<Direction> getMovementFuture();
    void onWorldChanged();
}
