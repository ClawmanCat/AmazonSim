package com.groep15.amazonsim.models;

import com.groep15.amazonsim.models.ai.IWorldActor;
import com.groep15.amazonsim.utility.Vec2i;
import com.groep15.amazonsim.utility.WorldGraph;
import org.json.simple.JSONObject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class World implements Model {
    private int tick;
    private List<Object3D> worldObjects;
    private int w, h;
    private WorldGraph graph;
    private JSONObject settings;

    PropertyChangeSupport pcs = new PropertyChangeSupport(this);


    public World(int w, int h) {
        this.worldObjects = new ArrayList<>();
        this.w = w;
        this.h = h;
        this.graph = new WorldGraph(this);
        this.settings = new JSONObject();
    }


    public void loadSettings(JSONObject settings) {
        this.settings = settings;
    }


    public Object getSetting(String key) {
        List<String> keySegments = Arrays.asList(key.split("\\."));

        Object o = settings;
        for (String segment : keySegments) o = ((JSONObject) o).get(segment);

        return o;
    }


    @Override
    public void update() {
        for (Object3D object : this.worldObjects) {
            if (object.update()) {
                pcs.firePropertyChange(Model.UPDATE_COMMAND, null, new ProxyObject3D(object));
            }
        }

        for (Object3D object : this.worldObjects) {
            if (object.dirty) {
                pcs.firePropertyChange(Model.UPDATE_COMMAND, null, new ProxyObject3D(object));
                object.dirty = false;
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
        return this.worldObjects.stream()
                .map(ProxyObject3D::new)
                .collect(Collectors.toList());
    }


    public List<Object3D> getWorldObjectsModifyiable() {
        return new ArrayList<>(this.worldObjects);
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


    public List<IWorldActor> getActors() {
        List<IWorldActor> actors = new ArrayList<>();
        for (Object3D o : this.worldObjects) if (o instanceof IWorldActor) actors.add((IWorldActor) o);

        return actors;
    }
}