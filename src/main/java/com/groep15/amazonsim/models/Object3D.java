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
        if (o.containsKey("type") && !o.get("type").equals(getName().toLowerCase()))
            throw new IllegalArgumentException("Attempted to create object of type " + getName() + " with JSON data for type " + o.get("type") + ".");

        if (o.containsKey("properties")) o = (JSONObject) o.get("properties");
        else return;    // No properties = nothing to deserialize.

        this.uuid = (o.get("uuid") == null) ? this.uuid : (UUID) o.get("uuid");

        this.x = (o.get("x") == null) ? this.x : (Double) o.get("x");
        this.y = (o.get("y") == null) ? this.y : (Double) o.get("y");
        this.z = (o.get("z") == null) ? this.z : (Double) o.get("z");

        this.rx = (o.get("rotationX") == null) ? this.rx : (Double) o.get("rotationX");
        this.ry = (o.get("rotationY") == null) ? this.ry : (Double) o.get("rotationY");
        this.rz = (o.get("rotationZ") == null) ? this.rz : (Double) o.get("rotationZ");
    }

    // Null coalescing function
    // TODO: Move to utility class.
    protected static Object Coal(Object... objs) {
        for (Object o : objs) if (o != null) return o;
        return null;
    }
}