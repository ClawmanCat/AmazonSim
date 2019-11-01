package com.groep15.amazonsim.models;

import com.groep15.amazonsim.ai.ActionIdle;
import com.groep15.amazonsim.ai.IWorldAction;
import com.groep15.amazonsim.ai.IWorldActor;

public class Truck extends Object3D implements IWorldActor {
    private IWorldAction action;
    private boolean changedFromAction;

    public Truck(World world) {
        super(world);

        this.passable = true;
        this.action = new ActionIdle();
        this.changedFromAction = this.action.onActionStart(this);
    }

    @Override
    public boolean update() {
        if (this.action.progress(this) || changedFromAction) {
            changedFromAction = false;
            return true;
        }

        return false;
    }

    @Override
    public double getSpeed() {
        return 0.25;
    }

    @Override
    public void setAction(IWorldAction action) {
        this.changedFromAction |= this.action.onActionDone(this);
        this.action = action;
        this.changedFromAction |= this.action.onActionStart(this);
    }

    @Override
    public IWorldAction getAction() {
        return this.action;
    }

}
