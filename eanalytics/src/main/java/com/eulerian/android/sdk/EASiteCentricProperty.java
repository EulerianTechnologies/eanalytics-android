package com.eulerian.android.sdk;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Francois Rouault on 10/03/2015.
 */
public class EASiteCentricProperty {

    private final JSONObject mJSONObject;

    EASiteCentricProperty(Builder builder) {
        mJSONObject = builder.jsonObject;
    }

    public JSONObject getJSONObject() {
        return mJSONObject;
    }

    /**
     * TODO: nothing is mandatory -> can be empty ?
     */
    public static class Builder {
        private final JSONObject jsonObject = new JSONObject();

        public Builder() {

        }

        public Builder set(String key, String... value) {
            JSONArray jsonArray = new JSONArray();
            for (String s : value) {
                jsonArray.put(s);
            }
            JSONUtils.put(jsonObject, key, jsonArray);
            return this;
        }

        public EASiteCentricProperty build() {
            return new EASiteCentricProperty(this);
        }
    }


}
