package com.groep15.amazonsim.models.worldobject;

import com.groep15.amazonsim.controllers.wms.WarehouseItem;
import com.groep15.amazonsim.models.World;
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

        json.put("item_count", items.size() / 2);

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

    public int getItemCount() {
        return items.size();
    }
}
