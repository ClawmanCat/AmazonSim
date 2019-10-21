package com.groep15.amazonsim.models;

import org.json.simple.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ProxyObject3D extends Object3D {
    private Object3D object;

    public ProxyObject3D(Object3D object) {
        super(object.world);
        this.object = object;
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
    public boolean update() {
        throw new NotImplementedException();
    }
}