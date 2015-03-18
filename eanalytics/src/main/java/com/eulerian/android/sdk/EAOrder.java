package com.eulerian.android.sdk;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Francois Rouault on 12/03/2015.
 */
public class EAOrder extends EAProperties {

    private static final String KEY_REF = "ref";
    private static final String KEY_SALE_AMOUNT = "amount";
    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_ESTIMATE_REF = "estimateref";
    private static final String KEY_TYPE = "type";
    private static final String KEY_PAYMENT = "payment";
    private static final String KEY_PRODUCTS = "products";
    private static final String KEY_PRODUCT_AMOUNT = "amount";
    private static final String KEY_PRODUCT_QUANTITY = "quantity";

    /**
     * Use {@link EAOrder.Builder} instead
     *
     * @param builder
     */
    protected EAOrder(Builder builder) {
        super(builder);
    }

    public static class Builder extends EAProperties.Builder<Builder> {

        private final JSONArray products = new JSONArray();

        public Builder(String path, String ref) {
            super(path);
            set(KEY_REF, ref);
        }

        public Builder setAmount(int amount) {
            set(KEY_SALE_AMOUNT, String.valueOf(amount));
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

        public Builder setPayment(String payment) {
            set(KEY_PAYMENT, payment);
            return this;
        }

        public Builder setEstimateRef(String estimateRef) {
            set(KEY_ESTIMATE_REF, estimateRef);
            return this;
        }

        public Builder addProduct(Product product, int amount, int quantity) {
            EALog.assertCondition(quantity > 0, "Quantity must be > 0");
            JSONObject productJson = product.getJson();
            JSONUtils.put(productJson, KEY_PRODUCT_AMOUNT, amount);
            JSONUtils.put(productJson, KEY_PRODUCT_QUANTITY, quantity);
            products.put(productJson);
            return this;
        }

        public EAOrder build() {
            JSONUtils.put(properties, KEY_PRODUCTS, products);
            return new EAOrder(this);
        }
    }

}
