package com.groep15.amazonsim.models;

import com.sun.javafx.geom.Vec3d;
import org.json.simple.JSONObject;

import java.util.UUID;

public abstract class Object3D implements Updatable {
    protected World world;
    protected double x, y, z;
    protected double rx, ry, rz;
    protected UUID uuid;

    Object3D(World world) {
        this.world = world;
        this.x  = this.y  = this.z  = 0.0;
        this.rx = this.ry = this.rz = 0.0;
        this.uuid = UUID.randomUUID();
    }


    public UUID getID() { return this.uuid; }
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public Vec3d getPosition() { return new Vec3d(x, y, z); }
    public Vec3d getRotation() { return new Vec3d(rx, ry, rz); }

    public void setPosition(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }

    public void setRotation(double x, double y, double z) {
        this.rx = x; this.ry = y; this.rz = z;
    }


    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("uuid", this.uuid.toString());
        result.put("type", this.getName());

        result.put("x", this.x);
        result.put("y", this.y);
        result.put("z", this.z);

        result.put("rotationX", this.rx);
        result.put("rotationY", this.ry);
        result.put("rotationZ", this.rz);

        return result;
    }

    public void fromJSON(JSONObject o) {
        if (o.containsKey("type") && o.get("type") != getName())
            throw new IllegalArgumentException("Attempted to create object of type " + getName() + " with JSON data for type " + o.get("type") + ".");

        if (o.containsKey("properties")) o = (JSONObject) o.get("properties");
        else return;    // No properties = nothing to deserialize.

        this.uuid = (UUID) Coal(o.get("uuid"), UUID.randomUUID());

        this.x = (Double) Coal(o.get("x"), 0.0);
        this.y = (Double) Coal(o.get("y"), 0.0);
        this.z = (Double) Coal(o.get("z"), 0.0);

        this.rx = (Double) Coal(o.get("rotationX"), 0.0);
        this.ry = (Double) Coal(o.get("rotationY"), 0.0);
        this.rz = (Double) Coal(o.get("rotationZ"), 0.0);
    }

    // Null coalescing function
    private static Object Coal(Object... objs) {
        for (Object o : objs) if (o != null) return o;
        return null;
    }
}