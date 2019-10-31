package com.groep15.amazonsim.controllers.wms;

import java.util.*;

public class WarehouseItemFactory {
    private static final String[] Products = new String[] {
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

    public static final WarehouseItemFactory instance = new WarehouseItemFactory();

    private Manufacturer[] Manufacturers = new Manufacturer[] {
            new Manufacturer("MelonCat Incorporated"),
            new Manufacturer("Katternije Limited"),
            new Manufacturer("Poezewoefke b.v. Cat-Related Products")
    };

    private Random rng = new Random();

    public WarehouseItemFactory() {
        // Randomly distribute available products over manufacturers.
        List<String> shuffled = new ArrayList<>(Arrays.asList(Products));
        Collections.shuffle(shuffled);

        for (int i = 0; i < shuffled.size(); ++i) {
            Manufacturers[i % Manufacturers.length].addProduct(shuffled.get(i));
        }
    }

    public List<WarehouseItem> create(int count) {
        List<WarehouseItem> items = new ArrayList<>();

        for (int i = 0; i < count; ++i) {
            Manufacturer mf = Manufacturers[rng.nextInt(Manufacturers.length)];
            Product p = mf.getProducts().get(rng.nextInt(mf.getProducts().size()));

            WarehouseItem item =  new WarehouseItem(p);
            items.add(item);
        }

        return items;
    }
}
