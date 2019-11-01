package com.groep15.amazonsim.models;

import com.groep15.amazonsim.wms.WarehouseItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Shelf extends Object3D {
    private List<WarehouseItem> items = new ArrayList<>();
    private boolean contentsChanged = false;

    public Shelf(World world) {
        super(world);

        this.sy = (2.4375 / 2.0);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("item_count", items.size());

        JSONArray itemJSON = new JSONArray();
        for (WarehouseItem i : items) itemJSON.add(i.toJSON());

        json.put("items", itemJSON);

        return json;
    }

    @Override
    public boolean update() {
        if (contentsChanged) {
            contentsChanged = false;
            return true;
        }

        return false;
    }

    public void addItem(WarehouseItem item) {
        items.add(item);
        contentsChanged = true;
    }

    public void removeItem(WarehouseItem item) {
        items.remove(item);
        contentsChanged = true;
    }

    public WarehouseItem getItem(int SKU) {
        return items.stream()
                .filter(x -> x.SKU == SKU)
                .findFirst()
                .get();
    }

    public int getItemCount() {
        return items.size();
    }
}
