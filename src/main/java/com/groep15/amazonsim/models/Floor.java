package com.groep15.amazonsim.models;

import org.json.simple.JSONObject;

public class Floor extends Object3D {
    private double w, h;

    public Floor(World world) {
        super(world);

        this.passable = true;
        this.rx = Math.PI / 2.0;
        this.w = 1;
        this.h = 1;
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject o = super.toJSON();
        o.put("w", w);
        o.put("h", h);

        return o;
    }

    @Override
    public void fromJSON(JSONObject o) {
        super.fromJSON(o);

        this.w = (o.get("w") == null) ? this.w : (Double) o.get("w");
        this.h = (o.get("h") == null) ? this.h : (Double) o.get("h");
    }
}
