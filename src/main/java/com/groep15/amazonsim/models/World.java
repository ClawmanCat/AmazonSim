package com.groep15.amazonsim.models;

import com.groep15.amazonsim.ai.IWorldActor;
import com.groep15.amazonsim.base.App;
import com.groep15.amazonsim.utility.Vec2i;
import com.groep15.amazonsim.utility.WorldGraph;
import com.groep15.amazonsim.wms.ObjectTracker;
import com.groep15.amazonsim.wms.WarehouseItemFactory;
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
    private ObjectTracker tracker;
    private WarehouseItemFactory factory;

    PropertyChangeSupport pcs = new PropertyChangeSupport(this);


    public World(int w, int h) {
        this.worldObjects = new ArrayList<>();
        this.w = w;
        this.h = h;
        this.graph = new WorldGraph(this);
        this.settings = new JSONObject();
        this.factory = new WarehouseItemFactory();
    }


    private void postInit() {
        // Object may be modified between constructor and 1st tick.
        // Stuff that isn't needed until the first tick may be added here.
        this.tracker = new ObjectTracker(
                this.worldObjects.stream()
                        .filter(x -> x instanceof Shelf)
                        .map(x -> (Shelf) x)
                        .collect(Collectors.toList())
        );
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
        if (App.Controller != null && !App.Controller.hasViews()) return;

        if (tick == 0) postInit();

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


    public ObjectTracker getTracker() {
        return tracker;
    }


    public List<IWorldActor> getActors() {
        List<IWorldActor> actors = new ArrayList<>();
        for (Object3D o : this.worldObjects) if (o instanceof IWorldActor) actors.add((IWorldActor) o);

        return actors;
    }
}