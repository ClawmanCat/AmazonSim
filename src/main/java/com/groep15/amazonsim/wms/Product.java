package com.groep15.amazonsim.wms;

public class Product {
    public final String name;
    public final Manufacturer manufacturer;

    public Product(String name, Manufacturer manufacturer) {
        this.name = name;
        this.manufacturer = manufacturer;
    }
}
