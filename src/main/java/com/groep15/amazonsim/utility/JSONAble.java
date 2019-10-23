package com.groep15.amazonsim.utility;

import org.json.simple.JSONObject;

public interface JSONAble {
    JSONObject toJSON();
    void fromJSON(JSONObject o);
}
