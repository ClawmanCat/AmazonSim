package com.groep15.amazonsim.wms;

import com.groep15.amazonsim.models.Shelf;
import com.groep15.amazonsim.utility.Wrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ObjectTracker {
    public static final int ShelfCapacity = 8;

    private HashMap<Integer, Shelf> SKULocations;
    private HashMap<Shelf, Wrapper<Integer>> shelfSpace;

    public ObjectTracker(List<Shelf> shelves) {
        this.SKULocations = new HashMap<>();

        this.shelfSpace = new HashMap<>();
        for (Shelf s : shelves) this.shelfSpace.put(s, new Wrapper<>(ShelfCapacity));
    }

    public boolean addItem(WarehouseItem item, Shelf where) {
        Wrapper<Integer> space = shelfSpace.get(where);
        if (space.value == 0) return false;
        --space.value;

        SKULocations.put(item.SKU, where);
        SKULocations.get(item.SKU).addItem(item);

        return true;
    }

    public void removeItem(WarehouseItem item) {
        ++shelfSpace.get(SKULocations.get(item.SKU)).value;

        SKULocations.get(item.SKU).removeItem(item);
        SKULocations.remove(item.SKU).removeItem(item);
    }

    public WarehouseItem randomItem() {
        Random r = new Random();

        Map.Entry<Integer, Shelf> entry = (Map.Entry<Integer, Shelf>) SKULocations.entrySet().toArray()[r.nextInt(SKULocations.size())];
        return entry.getValue().getItem(entry.getKey());
    }

    public Shelf findItem(WarehouseItem item) {
        return SKULocations.get(item.SKU);
    }

    public Shelf findShelfWithSpace() {
        for (Map.Entry<Shelf, Wrapper<Integer>> kv : shelfSpace.entrySet()) {
            if (kv.getValue().value > 0) return kv.getKey();
        }

        return null;
    }

    public Shelf randomShelf() {
        Random r = new Random();

        Map.Entry<Shelf, Wrapper<Integer>> entry = (Map.Entry<Shelf, Wrapper<Integer>>) shelfSpace.entrySet().toArray()[r.nextInt(shelfSpace.size())];
        return entry.getKey();
    }

    public int storedItemCount() {
        return SKULocations.size();
    }
}
