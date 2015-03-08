package com.eulerian.android.sdk.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Francois Rouault on 08/03/2015.
 */
public class EACart extends EAProperties {

    private List<EAProduct> mProducts;

    protected EACart() {

    }

    public void setProducts(List<EAProduct> products) {
        mProducts = products;
    }

    @Override
    public JSONObject toJson(boolean addInternalProperties) {
        JSONObject json = super.toJson(true);
        JSONArray jsonArray = new JSONArray();
        for (EAProduct item : mProducts) {
            jsonArray.put(item.toJson(false));
        }
        try {
            json.put("items", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static class Builder extends EAProperties.Builder<Builder> {
        private final List<EAProduct> products = new ArrayList<>();

        public Builder() {
            super();
            set(KEY_PROPERTY_TYPE, "cart");
        }

        public Builder addProductsToCart(EAProduct... products) {
            this.products.addAll(Arrays.asList(products));
            return this;
        }

        public Builder addProductToCart(EAProduct product) {
            products.add(product);
            return this;
        }

        public EACart build() {
            EACart res = new EACart();
            res.setHashmap(hashmap);
            res.setInternal(internal);
            res.setProducts(products);
            return res;
        }

    }

}
