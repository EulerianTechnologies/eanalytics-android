package com.eulerian.android.sdk;

/**
 * Created by Francois Rouault on 08/03/2015.
 */
public class EACart extends EAProperties {

    //    private List<EAProduct> mProducts;
//
    protected EACart(Builder builder) {
        super(builder);
    }
//
//    public void setProducts(List<EAProduct> products) {
//        mProducts = products;
//    }
//
//    @Override
//    public JSONObject getJson(boolean addInternalProperties) {
//        JSONObject json = super.getJson(true);
//        JSONArray jsonArray = new JSONArray();
//        for (EAProduct item : mProducts) {
//            jsonArray.put(item.getJson(false));
//        }
//        try {
//            json.put("items", jsonArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return json;
//    }
//
//    //-----------
//    //- BUILDER
//    //-----------
//
//    public static class Builder extends EAProperties.Builder<Builder> {
//        private final List<EAProduct> products = new ArrayList<>();
//
//        public Builder(String path, int newCustomer) {
//            super(path, newCustomer);
//        }
//
//        public Builder addProductsToCart(EAProduct... products) {
//            this.products.addAll(Arrays.asList(products));
//            return this;
//        }
//
//        public Builder addProductToCart(EAProduct product) {
//            products.add(product);
//            return this;
//        }
//
//        public EACart build() {
//            EACart res = new EACart(this);
//            return res;
//        }
//    }

}
