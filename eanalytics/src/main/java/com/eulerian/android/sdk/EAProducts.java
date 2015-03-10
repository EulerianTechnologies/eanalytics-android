package com.eulerian.android.sdk;

import org.json.JSONArray;

/**
 * Created by Francois Rouault on 11/03/2015.
 */
public class EAProducts extends EAProperties {

    private static final String KEY_PRODUCTS = "products";

    /**
     * Use {@link com.eulerian.android.sdk.EAProducts.Builder} instead
     *
     * @param builder
     */
    protected EAProducts(Builder builder) {
        super(builder);
    }

    public static class Builder extends EAProperties.Builder<Builder> {
        private final JSONArray jsonProducts = new JSONArray();

        public Builder(String path) {
            super(path);
        }

        public Builder addProduct(Product product) {
            jsonProducts.put(product.getJson());
            return this;
        }

        public EAProducts build() {
            JSONUtils.put(properties, KEY_PRODUCTS, jsonProducts);
            EAProducts result = new EAProducts(this);
            return result;
        }
    }
}
