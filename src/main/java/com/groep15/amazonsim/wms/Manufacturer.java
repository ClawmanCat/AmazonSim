package com.groep15.amazonsim.wms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Manufacturer {
    public final String name;
    private List<Product> soldProducts;

    public Manufacturer(String name) {
        this.name = name;
        this.soldProducts = new ArrayList<>();
    }

    public void addProduct(String name) {
        this.soldProducts.add(new Product(name, this));
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(this.soldProducts);
    }
}
