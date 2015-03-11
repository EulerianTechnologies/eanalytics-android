package com.eulerian.android.sdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /**
     * TODO: nothing is mandatory => object can be empty "action": {}. Is that correct ?
     */
    public static class Builder {
        private final JSONObject mainJson = new JSONObject();
        private final List<String> inStrings = new ArrayList<>();
        private final List<String> outStrings = new ArrayList<>();

        public Builder() {

        }

        public Builder setReference(String ref) {
            try {
                mainJson.put(KEY_REF, ref);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder addIn(String... ins) {
            inStrings.addAll(Arrays.asList(ins));
            return this;
        }

        public Builder addOut(String... outs) {
            outStrings.addAll(Arrays.asList(outs));
            return this;
        }

        public Action build() {
            if (inStrings.size() == 1) {
                JSONUtils.put(mainJson, KEY_IN, inStrings.get(0));
            } else if (!inStrings.isEmpty()) {
                JSONArray inArray = new JSONArray();
                for (String in : inStrings) {
                    inArray.put(in);
                }
                JSONUtils.put(mainJson, KEY_IN, inArray);
            }
            if (outStrings.size() == 1) {
                JSONUtils.put(mainJson, KEY_OUT, outStrings.get(0));
            } else if (!outStrings.isEmpty()) {
                JSONArray outArray = new JSONArray();
                for (String out : outStrings) {
                    outArray.put(out);
                }
                JSONUtils.put(mainJson, KEY_OUT, outArray);
            }
            return new Action(this);
        }
    }


}
