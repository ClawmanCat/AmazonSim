package com.groep15.amazonsim.ai;

public interface IWorldAction {
    boolean progress(IWorldActor obj);

    boolean onActionStart(IWorldActor obj);
    boolean onActionDone(IWorldActor obj);
}
