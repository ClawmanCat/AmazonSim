package com.groep15.amazonsim.models;

import org.json.simple.JSONObject;

public class Floor extends Object3D {
    private double w, h;

    public Floor(World world) {
        super(world);

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

        this.w = (Double) Coal(o.get("w"), this.w);
        this.h = (Double) Coal(o.get("h"), this.h);
    }
}
