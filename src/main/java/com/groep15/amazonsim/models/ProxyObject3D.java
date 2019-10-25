package com.groep15.amazonsim.models;

import com.groep15.amazonsim.utility.Vec3d;
import org.json.simple.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.UUID;

public class ProxyObject3D extends Object3D {
    private Object3D object;

    public ProxyObject3D(Object3D object) {
        super(object.world);
        this.object = object;
    }

    @Override
    public UUID getID() {
        return this.object.getID();
    }

    @Override
    public String getName() {
        return this.object.getName();
    }

    @Override
    public Vec3d getPosition() {
        return this.object.getPosition();
    }

    @Override
    public Vec3d getRotation() {
        return this.object.getRotation();
    }

    @Override
    public Vec3d getSize() {
        return this.object.getSize();
    }

    @Override
    public boolean getIsPassable() {
        return this.object.getIsPassable();
    }

    @Override
    public void setPosition(double x, double y, double z) {
        throw new NotImplementedException();
    }

    @Override
    public void setRotation(double x, double y, double z) {
        throw new NotImplementedException();
    }

    @Override
    public void fromJSON(JSONObject o) {
        throw new NotImplementedException();
    }

    @Override
    public JSONObject toJSON() {
        return object.toJSON();
    }

    @Override
    public boolean update() {
        throw new NotImplementedException();
    }
}