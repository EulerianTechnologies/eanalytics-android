package com.eulerian.android.sdk;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Francois Rouault on 07/03/2015.
 */
public class EAProperties {

    public static final String KEY_GENERIC_FOR_PROPERTY_1 = "ea-key1";
    public static final String KEY_GENERIC_FOR_PROPERTY_2 = "ea-key2";
    public static final String PROPERTY_TYPE_PRODUCT = "product";
    public static final String PROPERTY_TYPE_CART = "cart";
    protected static final String KEY_PROPERTY_TYPE = "property-type";
    protected static final String KEY_EOS = "eos-type";
    protected static final String KEY_EHW = "ehw";
    protected static final String KEY_URL = "url";
    protected static final String KEY_APPNAME = "ea-appname";
    protected static final String KEY_EPOCH = "epoch";
    protected static final String KEY_EUIDL = "euidl";
    protected static final String KEY_LATITUDE = "ea-lat";
    protected static final String KEY_LONGITUDE = "ea-lon";

    private Map<String, String> mHashmap;
    private Map<String, String> mInternalProperties;

    /**
     * Use {@link EAProperties.Builder} instead
     */
    protected EAProperties() {
    }

    protected void setInternal(Map<String, String> internalProperties) {
        this.mInternalProperties = internalProperties;
    }

    protected void setProperties(Map<String, String> hashmap) {
        this.mHashmap = hashmap;
    }

    //TODO: just for the demo. Will not be available to developer, keep the magic.
    public JSONObject toJson(boolean addInternalProperties) {
        JSONObject json = new JSONObject();
        for (String key : mHashmap.keySet()) {
            try {
                String value = mHashmap.get(key);
                json.put(key, value == null ? JSONObject.NULL.toString() : value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (addInternalProperties) {
            for (String key : mInternalProperties.keySet()) {
                try {
                    String value = mInternalProperties.get(key);
                    json.put(key, value == null ? JSONObject.NULL.toString() : value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return json;
    }

    //-----------
    //- BUILDER
    //-----------

    public static class Builder<T extends Builder> {

        protected final Map<String, String> properties = new HashMap<>();
        protected final Map<String, String> internal = new HashMap<>();

        /**
         * @param propertyType Use the set of predefined property type in {@link com.eulerian.android.sdk
         *                     .EAProperties}. For instance {@link #PROPERTY_TYPE_CART}. If you plan to use {@link
         *                     #PROPERTY_TYPE_CART}, use the convenience class {@link com.eulerian.android.sdk
         *                     .EACart} instead.
         */
        public Builder(String propertyType) {
            // internal object properties
            internal.put(KEY_EOS, "Android" + Build.VERSION.RELEASE);
            internal.put(KEY_EHW, Build.MANUFACTURER + " " + Build.MODEL);
            putEuidl();
            String packageName = EAnalytics.getContext().getApplicationInfo().packageName;
            internal.put(KEY_URL, "http://" + packageName);
            internal.put(KEY_APPNAME, packageName);
            internal.put("ea-android-islat", String.valueOf(EAnalytics.sAdInfoIsLAT));
            internal.put("ea-android-adid", EAnalytics.sAdInfoId);
            // object properties
            setProperty(KEY_EPOCH, String.valueOf(System.currentTimeMillis()));
            setProperty(KEY_PROPERTY_TYPE, propertyType);
        }

        /**
         * @param key   the key. Use the set of predefined keys in the corresponding class,
         *              or any other given key. For instance {@link #KEY_GENERIC_FOR_PROPERTY_1} or
         *              {@link EAProduct#KEY_1_SPECIFIC_FOR_PRODUCT}, etc...
         * @param value
         * @return
         */
        public synchronized T setProperty(String key, String value) {
            properties.put(key, value);
            return (T) this;
        }

        private void putEuidl() {
            TelephonyManager telephonyManager = (TelephonyManager) EAnalytics.getContext().getSystemService(Context
                    .TELEPHONY_SERVICE);
            internal.put(KEY_EUIDL, telephonyManager != null && telephonyManager.getDeviceId() != null ?
                    telephonyManager.getDeviceId() : Settings.Secure.getString(EAnalytics.getContext()
                    .getContentResolver(), Settings.Secure.ANDROID_ID));
        }

        public T setLocation(double latitude, double longitude) {
            internal.put(KEY_LATITUDE, String.valueOf(latitude));
            internal.put(KEY_LONGITUDE, String.valueOf(longitude));
            return (T) this;
        }

        public EAProperties build() {
            EAProperties res = new EAProperties();
            res.setProperties(properties);
            res.setInternal(internal);
            return res;
        }
    }
}
