package com.groep15.amazonsim.models;

import com.groep15.amazonsim.models.worldobject.Object3D;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface Model {
    public static final String UPDATE_COMMAND = "object_update";

    void update();
    void addObserver(PropertyChangeListener pcl);
    List<Object3D> getWorldObjectsAsList();
}