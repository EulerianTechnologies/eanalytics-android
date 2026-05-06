package com.eulerian.android.sdk;

import android.os.Build;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class EATpClick extends EAProperties {

    public static final String KEY_DYNTPCLICK = "dyntpclick";
    public static final String KEY_PRODUCT_REF = "ecprdr";
    public static final String KEY_PRODUCT_POS = "ecpos";
    public static final String KEY_TOTAL_PRODUCT = "ecnbr";
    public static final String KEY_TPCLICKPRODUCT = "tpclickproduct";

    public final String type = "EATpClick";

    private String siteName = "";
    private String campaignName = "";
    private String placement = "";
    private String publisher = "";
    private String media = "";
    private String category = "";
    private String url = "";

    private Product product = new Product("", 0, null);

    private EATpClick(Builder builder) {
        super(builder);

        this.siteName = builder.siteName;
        this.campaignName = builder.campaignName;
        this.placement = builder.placement;
        this.publisher = builder.publisher;
        this.media = builder.media;
        this.category = builder.category;
        this.url = builder.url;
        this.product = builder.product;
    }

    public static EATpClick fromRawData(Map<String, Object> rawData) {
        Map<String, Object> properties = (Map<String, Object>) rawData.get("properties");

        Builder builder = new Builder(
                rawData.get("path") != null ? rawData.get("path").toString() : ""
        );

        if (properties != null) {
            Object propsObj = properties.get(KEY_DYNTPCLICK);

            if (propsObj instanceof Map) {
                Map<String, Object> props = (Map<String, Object>) propsObj;

                if (props.get("siteName") != null)
                    builder.setSiteName(props.get("siteName").toString());

                if (props.get("campaign") != null)
                    builder.setCampaign(props.get("campaign").toString());

                if (props.get("placement") != null)
                    builder.setPlacement(props.get("placement").toString());

                if (props.get("publisher") != null)
                    builder.setPublisher(props.get("publisher").toString());

                if (props.get("media") != null)
                    builder.setMedia(props.get("media").toString());

                if (props.get("category") != null)
                    builder.setCategory(props.get("category").toString());

                if (props.get("url") != null)
                    builder.setUrl(props.get("url").toString());
            }

            Object productObj = properties.get(KEY_TPCLICKPRODUCT);
            if (productObj instanceof Map) {
                Map<String, Object> p = (Map<String, Object>) productObj;

                String ref = p.get("ref") != null ? p.get("ref").toString() : "";
                int position = p.get("position") != null ? ((Number) p.get("position")).intValue() : 0;
                Integer total = p.get("totalProducts") != null
                        ? ((Number) p.get("totalProducts")).intValue()
                        : null;

                builder.setProduct(ref, position, total);
            }
        }

        return builder.build();
    }

    public String toQueryString() throws UnsupportedEncodingException {
        int randomNum = new Random().nextInt(1_000_000_000);

        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append(encode(siteName)).append("/")
                .append(encode(campaignName)).append("/")
                .append(encode(placement)).append("/")
                .append(encode(siteName)).append("/generic/")
                .append(randomNum)
                .append("?");

        urlBuilder.append(KEY_PRODUCT_REF)
                .append("=")
                .append(encode(product.ref))
                .append("&");

        urlBuilder.append(KEY_PRODUCT_POS)
                .append("=")
                .append(encode(String.valueOf(product.position)))
                .append("&");

        if (product.totalProducts != null) {
            urlBuilder.append(KEY_TOTAL_PRODUCT)
                    .append("=")
                    .append(encode(String.valueOf(product.totalProducts)))
                    .append("&");
        }

        if (url != null && !url.isEmpty()) {
            urlBuilder.append("url=").append(encode(url)).append("&");
        }

        return urlBuilder.toString();
    }

    private String encode(String value) throws UnsupportedEncodingException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return URLEncoder.encode(value, String.valueOf(StandardCharsets.UTF_8));
        }
        return null;
    }

    public static class Product {
        public String ref;
        public int position;
        public Integer totalProducts;

        public Product(String ref, int position, Integer totalProducts) {
            this.ref = ref;
            this.position = position;
            this.totalProducts = totalProducts;
        }
    }

    public static class Builder extends EAProperties.Builder<EATpClick.Builder> {

        private String siteName = "";
        private String campaignName = "";
        private String placement = "";
        private String publisher = "";
        private String media = "";
        private String category = "";
        private String url = "";

        private Product product = new Product("", 0, null);

        public Builder(String path) {
            super(path);
        }

        public Builder setSiteName(String value) {
            this.siteName = value;
            return this;
        }

        public Builder setCampaign(String value) {
            this.campaignName = value;
            return this;
        }

        public Builder setPlacement(String value) {
            this.placement = value;
            return this;
        }

        public Builder setPublisher(String value) {
            this.publisher = value;
            return this;
        }

        public Builder setMedia(String value) {
            this.media = value;
            return this;
        }

        public Builder setCategory(String value) {
            this.category = value;
            return this;
        }

        public Builder setUrl(String value) {
            this.url = value;
            return this;
        }

        public Builder setProduct(String ref, int position) {
            return setProduct(ref, position, null);
        }

        public Builder setProduct(String ref, int position, Integer totalProducts) {
            this.product = new Product(ref, position, totalProducts);
            return this;
        }

        public EATpClick build() {
            EATpClick instance = new EATpClick(this);

            try {
                this.properties.put(KEY_DYNTPCLICK, Arrays.asList(
                        siteName,
                        campaignName,
                        placement,
                        publisher,
                        media,
                        category,
                        product
                ));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return instance;
        }
    }
}
