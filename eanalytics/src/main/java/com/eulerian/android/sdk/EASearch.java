package com.eulerian.android.sdk;

import org.json.JSONObject;

/**
 * Created by Francois Rouault on 11/03/2015.
 */
public class EASearch extends EAProperties {

    private static final String KEY_NAME = "name";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_PARAMS = "params";
    private static final String KEY_SEARCH_ENGINE = "isearchengine";

    /**
     * Use {@link com.eulerian.android.sdk.EASearch.Builder} instead
     *
     * @param builder
     */
    protected EASearch(Builder builder) {
        super(builder);
    }

    public static class Builder extends EAProperties.Builder<Builder> {
        private final JSONObject engine = new JSONObject();

        public Builder(String path, String name) {
            super(path);
            JSONUtils.put(engine, KEY_NAME, name);
        }

        public Builder setResults(int results) {
            JSONUtils.put(engine, KEY_RESULTS, results);
            return this;
        }

        public Builder setParams(Params params) {
            JSONUtils.put(engine, KEY_PARAMS, params.getJson());
            return this;
        }

        public EASearch build() {
            JSONUtils.put(properties, KEY_SEARCH_ENGINE, engine);
            return new EASearch(this);
        }

    }
}
