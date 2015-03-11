package com.eulerian.android.sdk;

import org.json.JSONObject;

/**
 * Created by Francois Rouault on 07/03/2015.
 */
public class Product {

    private static final String KEY_REF = "ref";
    private static final String KEY_NAME = "name";
    private static final String KEY_PARAMS = "params";

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

        public Builder(String reference) {
            JSONUtils.put(mainJson, KEY_REF, reference);
        }

        public Builder setName(String name) {
            JSONUtils.put(mainJson, KEY_NAME, name);
            return this;
        }

        public Builder setParams(Params params) {
            JSONUtils.put(mainJson, KEY_PARAMS, params.getJson());
            return this;
        }

        public Product build() {
            return new Product(this);
        }

    }
}
