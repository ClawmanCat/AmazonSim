package com.groep15.amazonsim.models;

import com.groep15.amazonsim.controllers.wms.WarehouseItem;

import java.util.ArrayList;
import java.util.List;

public class Shelf extends Object3D {
    private List<WarehouseItem> items = new ArrayList<>();

    public Shelf(World world) {
        super(world);

        this.sy = 1.15;
    }

    /*@Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("itemCount", items.size());

        JSONArray itemJSON = new JSONArray();
        for (WarehouseItem i : items) itemJSON.add(i.toJSON());

        json.put("items", itemJSON);

        return json;
    }*/

    @Override
    public boolean update() {
        return false;
    }

    public void addItem(WarehouseItem item) {
        items.add(item);
        dirty = true;
    }

    public void removeItem(WarehouseItem item) {
        items.remove(item);
        dirty = true;
    }

    public int getItemCount() {
        return items.size();
    }
}
