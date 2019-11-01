package com.groep15.amazonsim.controllers.wms;

import com.groep15.amazonsim.utility.JSONAble;
import org.json.simple.JSONObject;

public class WarehouseItem implements JSONAble {
    public final Product product;

    WarehouseItem(Product product) {
        this.product = product;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("manufacturer", product.manufacturer);
        result.put("name", product.name);

        return result;
    }

    @Override
    public void fromJSON(JSONObject o) { }
}
