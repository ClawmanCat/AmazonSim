package com.groep15.amazonsim.wms;

import com.groep15.amazonsim.models.Shelf;

import java.util.*;

public class WarehouseItemFactory {
    private static final String[] Products = {
            "Cat food (wet)",
            "Cat food (dry)",
            "Scratching post",
            "Catnip",
            "Scratching post XL Super Deluxe",
            "Litter box",
            "Cat litter",
            "Small controllable mech for cats",
            "Minigun attachment for cat-mech",
            "Jetpack attachment for cat-mech",
            "Grenade launcher attachment for cat-mech"
    };

    private Manufacturer[] Manufacturers = {
            new Manufacturer("MelonCat Incorporated"),
            new Manufacturer("Katternije Limited"),
            new Manufacturer("Poezewoefke b.v. Cat-Related Products")
    };

    private int nextSKU = 0;
    private Random rng = new Random();

    public WarehouseItemFactory() {
        // Randomly distribute available products over manufacturers.
        List<String> shuffled = Arrays.asList(Products);
        Collections.shuffle(shuffled);

        for (int i = 0; i < shuffled.size(); ++i) {
            Manufacturers[i % Manufacturers.length].addProduct(shuffled.get(i));
        }
    }

    public List<WarehouseItem> create(ObjectTracker tracker, int count, boolean track) {
        List<WarehouseItem> items = new ArrayList<>();

        for (int i = 0; i < count; ++i) {
            Manufacturer mf = Manufacturers[rng.nextInt(Manufacturers.length)];
            Product p = mf.getProducts().get(rng.nextInt(mf.getProducts().size()));

            WarehouseItem item = new WarehouseItem(nextSKU++, p);
            items.add(item);

            if (track) tracker.addItem(item, tracker.findShelfWithSpace());
        }

        return items;
    }

    public List<WarehouseItem> create(ObjectTracker tracker, Shelf shelf, int count, boolean track) {
        List<WarehouseItem> items = new ArrayList<>();

        if (shelf.getItemCount() + count > ObjectTracker.ShelfCapacity)
            throw new IllegalArgumentException("Cannot fill shelf with " + count + " items when it has room for " + (ObjectTracker.ShelfCapacity - shelf.getItemCount()));

        for (int i = 0; i < count; ++i) {
            Manufacturer mf = Manufacturers[rng.nextInt(Manufacturers.length)];
            Product p = mf.getProducts().get(rng.nextInt(mf.getProducts().size()));

            WarehouseItem item =  new WarehouseItem(nextSKU++, p);
            items.add(item);

            if (track) tracker.addItem(item, shelf);
        }

        return items;
    }
}
