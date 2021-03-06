package com.eulerian.android.sdk;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Francois Rouault on 08/03/2015.
 */
public class EACart extends EAProperties {

    private static final String KEY_SCART = "scart";
    private static final String KEY_CUMUL = "scartcumul";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_PRODUCTS = "products";

    /**
     * Use {@link com.eulerian.android.sdk.EACart.Builder} instead
     *
     * @param builder
     */
    protected EACart(Builder builder) {
        super(builder);
    }

    public static class Builder extends EAProperties.Builder<Builder> {

        private final JSONArray products = new JSONArray();

        public Builder(String path) {
            super(path);
            set(KEY_SCART, "1");
        }

        public Builder setCartCumul(boolean cumul) {
            set(KEY_CUMUL, cumul ? 1 : 0);
            return this;
        }

        public Builder addProduct(Product product, double amount, int quantity) {
            JSONObject productJson = product.getJson();
            JSONUtils.put(productJson, KEY_AMOUNT, amount);
            JSONUtils.put(productJson, KEY_QUANTITY, quantity);
            products.put(productJson);
            return this;
        }

        public EACart build() {
            JSONUtils.put(properties, KEY_PRODUCTS, products);
            return new EACart(this);
        }
    }

}
