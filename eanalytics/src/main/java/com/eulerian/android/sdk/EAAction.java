package com.eulerian.android.sdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Francois Rouault on 09/03/2015.
 */
public class EAAction {

    static final String KEY_REF = "ref";
    static final String KEY_IN = "in";
    static final String KEY_OUT = "out";

    private JSONObject mJSONObject;

    private EAAction(Builder builder) {
        mJSONObject = builder.jsonObject;
    }

    JSONObject getJSONObject() {
        return mJSONObject;
    }

    /**
     * TODO nothing is mandatory. Object can be empty "action": {} -> throw exception ?
     * TODO: removeOut removeIn ?
     */
    public static class Builder {
        private final JSONObject jsonObject = new JSONObject();
        private final List<String> inStrings = new ArrayList<>();
        private final List<String> outStrings = new ArrayList<>();

        public Builder() {

        }

        public Builder setRef(String ref) {
            try {
                jsonObject.put(KEY_REF, ref);
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

        public EAAction build() {
            if (inStrings.size() == 1) {
                JSONUtils.put(jsonObject, KEY_IN, inStrings.get(0));
            } else if (!inStrings.isEmpty()) {
                JSONArray inArray = new JSONArray();
                for (String in : inStrings) {
                    inArray.put(in);
                }
                JSONUtils.put(jsonObject, KEY_IN, inArray);
            }
            if (outStrings.size() == 1) {
                JSONUtils.put(jsonObject, KEY_OUT, outStrings.get(0));
            } else if (!outStrings.isEmpty()) {
                JSONArray outArray = new JSONArray();
                for (String out : outStrings) {
                    outArray.put(out);
                }
                JSONUtils.put(jsonObject, KEY_OUT, outArray);
            }
            return new EAAction(this);
        }
    }


}
