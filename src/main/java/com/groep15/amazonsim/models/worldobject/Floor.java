package com.groep15.amazonsim.models.worldobject;

import com.groep15.amazonsim.models.World;
import org.json.simple.JSONObject;

public class Floor extends Object3D {
    private double w, h;
    private String texture;

    public Floor(World world) {
        super(world);

        this.texture = null;
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
        if (texture != null) o.put("texture", texture);

        return o;
    }

    @Override
    public void fromJSON(JSONObject o) {
        super.fromJSON(o);

        if (o.get("properties") != null) {
            JSONObject params = (JSONObject) o.get("properties");

            this.w = (params.get("w") == null) ? this.w : (Double) params.get("w");
            this.h = (params.get("h") == null) ? this.h : (Double) params.get("h");

            this.texture = (params.get("texture") == null) ? this.texture : (String) params.get("texture");
        }
    }
}
