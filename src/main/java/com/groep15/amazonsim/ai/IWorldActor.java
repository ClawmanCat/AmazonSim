package com.groep15.amazonsim.ai;

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

    void setAction(IWorldAction action);
    IWorldAction getAction();
}
