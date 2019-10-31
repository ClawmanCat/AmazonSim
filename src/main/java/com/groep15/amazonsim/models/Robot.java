package com.groep15.amazonsim.models;

import com.groep15.amazonsim.models.ai.ActionIdle;
import com.groep15.amazonsim.models.ai.IWorldAction;
import com.groep15.amazonsim.models.ai.IWorldActor;

public class Robot extends Object3D implements IWorldActor {
    private IWorldAction action;
    private Object3D obj;

    public Robot(World world) {
        super(world);

        this.passable = true;
        this.sy = 0.15;

        this.action = new ActionIdle();
    }

    @Override
    public boolean update() {
        if (obj != null) {
            obj.setPosition(this.x, this.y  - this.sy + obj.sy + 0.005, this.z);
            obj.setRotation(this.rx, this.ry, this.rz);

            obj.dirty = true;
        }

        return this.action.progress(this);
    }

    @Override
    public double getSpeed() {
        return 0.05;
    }

    @Override
    public void setAction(IWorldAction action) {
        this.action = action;
    }

    @Override
    public IWorldAction getAction() {
        return this.action;
    }

    @Override
    public void grab(Object3D object) {
        this.obj = object;
        world.getWorldGraph().update();
    }

    @Override
    public void release() {
        this.obj.y = this.obj.sy;
        this.obj = null;

        world.getWorldGraph().update();
    }

    @Override
    public Object3D getHeldObject() {
        return this.obj;
    }
}