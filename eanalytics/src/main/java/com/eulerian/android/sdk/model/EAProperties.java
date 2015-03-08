package com.eulerian.android.sdk.model;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.eulerian.android.sdk.EAnalytics;

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

    public static class Builder<T extends Builder> {

        protected final Map<String, String> hashmap = new HashMap<>();
        protected final Map<String, String> internal = new HashMap<>();

        public Builder() {
            // internal object properties
            internal.put(KEY_EOS, "Android" + Build.VERSION.RELEASE);
            internal.put(KEY_EHW, Build.MANUFACTURER + " " + Build.MODEL);
            putEuidl();
            String packageName = EAnalytics.getContext().getApplicationInfo().packageName;
            internal.put(KEY_URL, "http://" + packageName);
            internal.put(KEY_APPNAME, packageName);
            //TODO internal.put("ea-android-islat", "??? TODO"); what is adInfo ?
            //TODO internal.put("ea-android-adid", adInfo.getId()); what is adInfo?
            // object properties
            set(KEY_EPOCH, String.valueOf(System.currentTimeMillis()));
            set(KEY_PROPERTY_TYPE, "property");
        }

        /**
         * @param key   the key. Use the set of predefined keys in the corresponding class,
         *              or any other given key. For instance {@link #KEY_GENERIC_FOR_PROPERTY_1} or
         *              {@link com.eulerian.android.sdk.model.EAProduct#KEY_1_SPECIFIC_FOR_PRODUCT}, etc...
         * @param value
         * @return
         */
        public synchronized T set(String key, String value) {
            hashmap.put(key, value);
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
            res.setHashmap(hashmap);
            res.setInternal(internal);
            return res;
        }
    }

    /**
     * Use {@link EAProperties.Builder} instead
     */
    protected EAProperties() {
    }

    protected void setInternal(Map<String, String> internalProperties) {
        this.mInternalProperties = internalProperties;
    }

    protected void setHashmap(Map<String, String> hashmap) {
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
}
