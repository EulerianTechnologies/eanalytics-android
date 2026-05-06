package com.eulerian.android.sdk;

import android.os.Build;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class EATpView extends EAProperties {

    public static final String KEY_DYNTPVIEW = "dyntpview";
    public static final String KEY_TPVIEWPRD = "tpviewprd";

    public final String type = "EATpView";

    private String siteName = "";
    private String campaignName = "";
    private String placement = "";
    private String publisher = "";
    private String media = "";
    private String category = "";
    private String url = "";

    private List<List<Object>> tpviewProducts = new ArrayList<>();

    private EATpView(Builder builder) {
        super(builder);

        this.siteName = builder.siteName;
        this.campaignName = builder.campaignName;
        this.placement = builder.placement;
        this.publisher = builder.publisher;
        this.media = builder.media;
        this.category = builder.category;
        this.url = builder.url;
        this.tpviewProducts = builder.tpviewProducts;
    }

    public static EATpView fromRawData(Map<String, Object> rawData) {
        Map<String, Object> properties = (Map<String, Object>) rawData.get("properties");

        Builder builder = new Builder(
                rawData.get("path") != null ? rawData.get("path").toString() : ""
        );

        if (properties != null) {
            Object propsObj = properties.get(KEY_DYNTPVIEW);

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

            Object productsObj = properties.get(KEY_TPVIEWPRD);
            if (productsObj instanceof List) {
                List<?> products = (List<?>) productsObj;

                for (Object p : products) {
                    if (p instanceof List) {
                        List<?> prod = (List<?>) p;

                        String ref = prod.get(0).toString();
                        Integer pos = prod.size() > 1 ? ((Number) prod.get(1)).intValue() : null;

                        builder.addProduct(ref, pos);
                    }
                }
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

        for (int i = 0; i < tpviewProducts.size(); i++) {
            List<Object> prod = tpviewProducts.get(i);

            String ref = prod.get(0).toString();
            Object pos = prod.size() > 1 ? prod.get(1) : null;

            urlBuilder.append("evprdr").append(i)
                    .append("=")
                    .append(encode(ref))
                    .append("&");

            if (pos != null) {
                urlBuilder.append("evprdpos").append(i)
                        .append("=")
                        .append(pos.toString())
                        .append("&");
            }
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

    public static class Builder extends EAProperties.Builder<EATpView.Builder> {

        private String siteName = "";
        private String campaignName = "";
        private String placement = "";
        private String publisher = "";
        private String media = "";
        private String category = "";
        private String url = "";
        private List<List<Object>> tpviewProducts = new ArrayList<>();

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

        public Builder addProduct(String ref, Integer position) {
            List<Object> prod = new ArrayList<>();
            prod.add(ref);

            if (position != null) {
                prod.add(position);
            }

            tpviewProducts.add(prod);
            return this;
        }

        public EATpView build() {
            EATpView instance = new EATpView(this);

            try {
                this.properties.put(KEY_DYNTPVIEW, Arrays.asList(
                        siteName,
                        campaignName,
                        placement,
                        publisher,
                        media,
                        category,
                        url
                ));

                this.properties.put(KEY_TPVIEWPRD, tpviewProducts);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return instance;
        }
    }
}