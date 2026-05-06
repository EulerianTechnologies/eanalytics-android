package com.eulerian.android.sdk;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Francois Rouault on 09/03/2015.
 * Can be added to any class {@link com.eulerian.android.sdk.EAProperties}
 */
public class Action {

    private static final String KEY_REF = "ref";
    private static final String KEY_MODE = "mode";
    private static final String KEY_LABEL = "label";
    private static final String KEY_PARAMS = "params";
    private static final String KEY_NAME = "name";

    private JSONObject mJson;

    private Action(Builder builder) {
        mJson = builder.mainJson;
    }

    JSONObject getJson() {
        return mJson;
    }

    public static class Builder {
        private final JSONObject mainJson = new JSONObject();

        public Builder setName(String name) {
            JSONUtils.put(mainJson, KEY_NAME, name);
            return this;
        }

        public Builder setReference(String ref) {
            JSONUtils.put(mainJson, KEY_REF, ref);
            return this;
        }

        public Builder setMode(String mode) {
            JSONUtils.put(mainJson, KEY_MODE, mode);
            return this;
        }

        public Builder setLabel(String label) {
            JSONUtils.put(mainJson, KEY_LABEL, label);
            return this;
        }

        public Builder setParams(Params params) {
            JSONUtils.put(mainJson, KEY_PARAMS, params.getJson());
            return this;
        }

        public Action build() {

            if (!mainJson.has(KEY_MODE)) {
                EALog.w(Action.class.getSimpleName() + ": setMode() must be called to create " +
                        "a valid Action.");
            }
            return new Action(this);
        }
    }


}
