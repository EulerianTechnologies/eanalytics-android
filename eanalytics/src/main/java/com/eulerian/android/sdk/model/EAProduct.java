package com.eulerian.android.sdk.model;

/**
 * Created by Francois Rouault on 07/03/2015.
 */
public class EAProduct extends EAProperties {

    public static final String KEY_1_SPECIFIC_FOR_PRODUCT = "ea-product-key1";
    protected static final String KEY_REF_ID = "ref-id";
    protected static final String KEY_PRICE = "ea-price";
    protected static final String KEY_CURRENCY = "ea-currency";

    /**
     * Use {@link com.eulerian.android.sdk.model.EAProduct.Builder} instead
     */
    protected EAProduct() {

    }

    public static class Builder extends EAProperties.Builder<Builder> {

        public Builder() {
            super();
            set(KEY_PROPERTY_TYPE, "product");
        }

        public Builder setId(String id) {
            set(KEY_REF_ID, id);
            return this;
        }

        public Builder setPrice(double price, String currency) {
            set(KEY_PRICE, String.valueOf(price));
            set(KEY_CURRENCY, currency);
            return this;
        }

        public EAProduct build() {
            EAProduct res = new EAProduct();
            res.setHashmap(this.hashmap);
            res.setInternal(internal);
            return res;
        }
    }
}
