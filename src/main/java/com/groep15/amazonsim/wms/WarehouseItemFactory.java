package com.groep15.amazonsim.wms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    public void create(ObjectTracker tracker, int count) {
        for (int i = 0; i < count; ++i) {
            Manufacturer mf = Manufacturers[rng.nextInt(Manufacturers.length)];
            Product p = mf.getProducts().get(rng.nextInt(mf.getProducts().size()));

            tracker.addItem(
                new WarehouseItem(nextSKU++, p),
                tracker.findShelfWithSpace()
            );
        }
    }
}
