package com.eulerian.android.sdk;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Francois Rouault on 07/09/2017.
 * Can be added to any class {@link EAProperties}
 */
public class SiteCentricCFlag {

    private final JSONObject mJson;

    SiteCentricCFlag(Builder builder) {
        mJson = builder.jsonUnderConstruction;
    }

    public JSONObject getJson() {
        return mJson;
    }

    public static class Builder {
        private final JSONObject jsonUnderConstruction = new JSONObject();

        public Builder() {

        }

        public SiteCentricCFlag.Builder set(String key, String... value) {
            JSONArray jsonArray = new JSONArray();
            for (String s : value) {
                jsonArray.put(s);
            }
            JSONUtils.put(jsonUnderConstruction, key, jsonArray);
            return this;
        }

        public SiteCentricCFlag build() {
            if (jsonUnderConstruction.length() == 0) {
                String clzName = SiteCentricCFlag.class.getSimpleName();
                EALog.w(clzName + ":  you might want to provide at least one set of key values to make " + clzName + " valid.");
            }
            return new SiteCentricCFlag(this);
        }
    }


}
