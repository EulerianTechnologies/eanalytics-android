package com.eulerian.android.sdk;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Francois Rouault on 10/03/2015.
 * Can be added to any class {@link com.eulerian.android.sdk.EAProperties}
 */
public class SiteCentricProperty {

    private final JSONObject mJson;

    SiteCentricProperty(Builder builder) {
        mJson = builder.mainJson;
    }

    public JSONObject getJson() {
        return mJson;
    }

    /**
     * TODO: nothing is mandatory => object can be empty "action": {}. Is that correct ?
     */
    public static class Builder {
        private final JSONObject mainJson = new JSONObject();

        public Builder() {

        }

        public Builder set(String key, String... value) {
            JSONArray jsonArray = new JSONArray();
            for (String s : value) {
                jsonArray.put(s);
            }
            JSONUtils.put(mainJson, key, jsonArray);
            return this;
        }

        public SiteCentricProperty build() {
            return new SiteCentricProperty(this);
        }
    }


}
