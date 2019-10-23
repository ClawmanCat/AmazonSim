package com.groep15.amazonsim.models;

import com.groep15.amazonsim.ai.ActionGoto;
import com.groep15.amazonsim.ai.ActionIdle;
import com.groep15.amazonsim.utility.Vec2i;
import com.groep15.amazonsim.utility.WorldGraph;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class World implements Model {
    private int tick;
    private List<Object3D> worldObjects;
    private int w, h;
    private WorldGraph graph;

    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public World(int w, int h) {
        this.worldObjects = new ArrayList<>();
        this.w = w;
        this.h = h;
        this.graph = new WorldGraph(this);
    }

    @Override
    public void update() {
        if (tick == 0) {
            System.out.println("Updated world graph");
            graph.update();
        }

        Object3D target = null;

        for (Object3D object : this.worldObjects) {
            if (object instanceof TestObject) target = object;

            if (object.update()) {
                if (object instanceof Robot && target != null && ((Robot) object).getAction() instanceof ActionIdle) {
                    ((Robot) object).setAction(new ActionGoto(new Vec2i(target.getPosition().x, target.getPosition().z)));
                }

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

    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public Vec2i getSize() {
        return new Vec2i(w, h);
    }
    public WorldGraph getWorldGraph() { return this.graph; }
}