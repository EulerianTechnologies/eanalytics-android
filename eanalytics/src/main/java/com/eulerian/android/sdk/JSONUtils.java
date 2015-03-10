package com.eulerian.android.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Francois Rouault on 10/03/2015.
 */
class JSONUtils {

    static void put(JSONObject json, String key, String value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static void put(JSONObject json, String key, int value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static void put(JSONObject json, String key, Object object) {
        try {
            json.put(key, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static JSONObject merge(JSONObject... json) {
        JSONObject merged = new JSONObject();
        for (JSONObject obj : json) {
            Iterator it = obj.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                try {
                    merged.put(key, obj.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return merged;
    }
}
