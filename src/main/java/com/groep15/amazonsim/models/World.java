package com.groep15.amazonsim.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class World implements Model {
    private int tick;
    private List<Object3D> worldObjects;

    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public World() {
        this.worldObjects = new ArrayList<>();
    }

    @Override
    public void update() {
        for (Object3D object : this.worldObjects) {
            if (object.update()) {
                pcs.firePropertyChange(Model.UPDATE_COMMAND, null, new ProxyObject3D(object));
            }
        }

        ++tick;
    }

    @Override
    public void addObserver(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    @Override
    public List<Object3D> getWorldObjectsAsList() {
        ArrayList<Object3D> returnList = new ArrayList<>();

        for(Object3D object : this.worldObjects) {
            returnList.add(new ProxyObject3D(object));
        }

        return returnList;
    }

    public void addWorldObject(Object3D o) {
        this.worldObjects.add(o);
    }

    public int getTickCount() {
        return tick;
    }
}