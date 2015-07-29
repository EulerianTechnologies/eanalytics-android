package com.eulerian.android.sdk;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Francois Rouault on 12/03/2015.
 */
public class EAEstimate extends EAProperties {

    private static final String KEY_ESTIMATE = "estimate";
    private static final String KEY_REF = "ref";
    private static final String KEY_ESTIMATE_AMOUNT = "amount";
    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_TYPE = "type";
    private static final String KEY_PRODUCTS = "products";
    private static final String KEY_PRODUCT_AMOUNT = "amount";
    private static final String KEY_QUANTITY = "quantity";

    /**
     * Use {@link com.eulerian.android.sdk.EAEstimate.Builder} instead
     *
     * @param builder
     */
    protected EAEstimate(Builder builder) {
        super(builder);
    }

    public static class Builder extends EAProperties.Builder<Builder> {

        private final JSONArray products = new JSONArray();

        public Builder(String path, String ref) {
            super(path);
            set(KEY_ESTIMATE, "1");
            set(KEY_REF, ref);
        }

        public Builder setAmount(int amount) {
            set(KEY_ESTIMATE_AMOUNT, String.valueOf(amount));
            return this;
        }

        public Builder setCurrency(String currency) {
            set(KEY_CURRENCY, currency);
            return this;
        }

        public Builder setCurrency(CurrencyISO currency) {
            set(KEY_CURRENCY, currency.value);
            return this;
        }

        public Builder setType(String type) {
            set(KEY_TYPE, type);
            return this;
        }

        public Builder addProduct(Product product, double amount, int quantity) {
            if (quantity <= 0) {
                EALog.w(EAEstimate.class.getSimpleName() + "#addProduct() : quantity might be > 0. Current is " +
                        quantity);
            }
            JSONObject productJson = product.getJson();
            JSONUtils.put(productJson, KEY_PRODUCT_AMOUNT, amount);
            JSONUtils.put(productJson, KEY_QUANTITY, quantity);
            products.put(productJson);
            return this;
        }

        public EAEstimate build() {
            JSONUtils.put(properties, KEY_PRODUCTS, products);
            return new EAEstimate(this);
        }
    }

}
