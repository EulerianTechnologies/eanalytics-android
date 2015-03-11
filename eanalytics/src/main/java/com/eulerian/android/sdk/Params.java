package com.eulerian.android.sdk;

import org.json.JSONObject;

/**
 * Created by Francois Rouault on 11/03/2015.
 */
public class Params {

    private final JSONObject mJson;

    public Params(Builder builder) {
        this.mJson = builder.mainJson;
    }

    public JSONObject getJson() {
        return mJson;
    }

    public static class Builder {
        private final JSONObject mainJson = new JSONObject();

        public Builder addParam(String key, String value) {
            JSONUtils.put(mainJson, key, value);
            return this;
        }

        public Builder addParam(String key, int value) {
            JSONUtils.put(mainJson, key, value);
            return this;
        }

        public Params build() {
            return new Params(this);
        }

    }


}
