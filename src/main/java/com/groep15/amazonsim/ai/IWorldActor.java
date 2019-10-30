package com.groep15.amazonsim.ai;

import com.groep15.amazonsim.models.Object3D;
import com.groep15.amazonsim.models.World;
import com.groep15.amazonsim.utility.Vec3d;

public interface IWorldActor {
    double getSpeed();

    Vec3d getPosition();
    Vec3d getRotation();
    Vec3d getSize();
    void setPosition(double x, double y, double z);
    void setRotation(double x, double y, double z);

    World getWorld();

    void setAction(com.groep15.amazonsim.ai.IWorldAction action);
    IWorldAction getAction();

    void grab(Object3D object);
    void release();
    Object3D getHeldObject();
}
