package com.groep15.amazonsim.models;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface Model {
    public static final String UPDATE_COMMAND = "object_update";

    void update();
    void addObserver(PropertyChangeListener pcl);
    List<Object3D> getWorldObjectsAsList();
}