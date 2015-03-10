package com.eulerian.android.sdk;

import org.json.JSONObject;

/**
 * Created by Francois Rouault on 07/03/2015.
 */
public class Product {

    private static final String KEY_REF = "ref";
    protected static final String KEY_NAME = "name";
    protected static final String KEY_PARAMS = "params";

    private JSONObject mJson;

    protected Product(Builder builder) {
        mJson = builder.mainJson;
    }

    public JSONObject getJson() {
        return mJson;
    }

    //-----------
    //- BUILDER
    //-----------

    public static class Builder {
        private final JSONObject mainJson = new JSONObject();
        private final JSONObject jsonParams = new JSONObject();

        public Builder(String reference) {
            JSONUtils.put(mainJson, KEY_REF, reference);
        }

        public Builder setName(String name) {
            JSONUtils.put(mainJson, KEY_NAME, name);
            return this;
        }

        public Builder addParams(String key, String value) {
            JSONUtils.put(jsonParams, key, value);
            return this;
        }

        public Builder addParams(String key, int value) {
            JSONUtils.put(jsonParams, key, value);
            return this;
        }

        public Product build() {
            JSONUtils.put(mainJson, KEY_PARAMS, jsonParams);
            Product res = new Product(this);
            return res;
        }

    }
}
