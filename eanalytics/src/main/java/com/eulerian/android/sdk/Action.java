package com.eulerian.android.sdk;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Francois Rouault on 09/03/2015.
 * Can be added to any class {@link com.eulerian.android.sdk.EAProperties}
 */
public class Action {

    private static final String KEY_REF = "ref";
    private static final String KEY_IN = "in";
    private static final String KEY_OUT = "out";

    private JSONObject mJson;

    private Action(Builder builder) {
        mJson = builder.mainJson;
    }

    JSONObject getJson() {
        return mJson;
    }

    public static class Builder {
        private final JSONObject mainJson = new JSONObject();
        private final JSONArray outsJson = new JSONArray();

        public Builder setReference(String ref) {
            JSONUtils.put(mainJson, KEY_REF, ref);
            return this;
        }

        public Builder setIn(String in) {
            JSONUtils.put(mainJson, KEY_IN, in);
            return this;
        }

        public Builder addOut(String... outs) {
            for (String out : outs) {
                outsJson.put(out);
            }
            return this;
        }

        public Action build() {
            if (outsJson.length() != 0) {
                JSONUtils.put(mainJson, KEY_OUT, outsJson);
            }
            if (!mainJson.has(KEY_IN) && !mainJson.has(KEY_OUT)) {
                EALog.w(Action.class.getSimpleName() + ": one of setIn() or addOut() method must be called to create " +
                        "a valid Action.");
            }
            return new Action(this);
        }
    }


}
