package com.groep15.amazonsim.wms;

import com.groep15.amazonsim.utility.JSONAble;
import org.json.simple.JSONObject;

public class WarehouseItem implements JSONAble {
    public final int SKU;
    public final Product product;

    WarehouseItem(int SKU, Product product) {
        this.SKU = SKU;
        this.product = product;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        result.put("SKU", SKU);
        result.put("manufacturer", product.manufacturer);
        result.put("name", product.name);

        return result;
    }

    @Override
    public void fromJSON(JSONObject o) { }
}
