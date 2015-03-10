package com.eulerian.android.sdk;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.json.JSONObject;

/**
 * Created by Francois Rouault on 07/03/2015.
 */
public class EAProperties {

    //- internal keys
    protected static final String KEY_INSTALL_REFERRER = "ea-android-referrer";
    protected static final String KEY_EOS = "eos";
    protected static final String KEY_EHW = "ehw";
    protected static final String KEY_ADINFO_IS_LAT = "ea-android-islat";
    protected static final String KEY_EUIDL = "euidl";
    protected static final String KEY_URL = "url";
    protected static final String KEY_APPNAME = "ea-appname";
    protected static final String KEY_ADINFO_ID = "ea-android-adid";
    protected static final String KEY_EPOCH = "ereplay-time";
    //- page keys
    protected static final String KEY_PAGE_LATITUDE = "ea-lat";
    protected static final String KEY_PAGE_LONGITUDE = "ea-lon";
    protected static final String KEY_PAGE_PATH = "path";
    protected static final String KEY_PAGE_EMAIL = "email";
    protected static final String KEY_PAGE_UID = "uid";
    protected static final String KEY_PAGE_PROFILE = "profile";
    protected static final String KEY_PAGE_GROUP = "pagegroup";
    protected static final String KEY_PAGE_ACTION = "action";
    protected static final String KEY_PAGE_PROPERTY = "property";
    protected static final String KEY_PAGE_NEW_CUSTOMER = "newcustomer";

    private JSONObject mProperties;
    private JSONObject mInternals;
    private JSONObject mPages;

    /**
     * Use {@link EAProperties.Builder} instead
     */
    protected EAProperties(Builder builder) {
        mInternals = builder.internals;
        mPages = builder.pages;
        mProperties = builder.properties;
    }

    /**
     * //TODO just for the demo
     */
    public JSONObject toJson(boolean withInternals) {
        JSONObject result;
        if (withInternals) {
            result = JSONUtils.merge(mInternals, mPages, mProperties);
        } else {
            result = JSONUtils.merge(mPages, mProperties);
        }
        return result;
    }

    //-----------
    //- BUILDER
    //-----------

    public static class Builder<T extends Builder> {

        protected final JSONObject properties = new JSONObject();
        protected final JSONObject internals = new JSONObject();
        protected final JSONObject pages = new JSONObject();

        public Builder(String path, int newCustomer) {
            initInteralParams();
            setPagePath(path);
            setPageNewCustomer(newCustomer);
        }

        private void initInteralParams() {
            JSONUtils.put(internals, KEY_INSTALL_REFERRER, EAnalytics.sInstallReferrer);
            JSONUtils.put(internals, KEY_EOS, "Android" + Build.VERSION.RELEASE);
            JSONUtils.put(internals, KEY_EHW, Build.MANUFACTURER + " " + Build.MODEL);
            TelephonyManager telephonyManager = (TelephonyManager) EAnalytics.getContext().getSystemService(Context
                    .TELEPHONY_SERVICE);
            JSONUtils.put(internals, KEY_EUIDL, telephonyManager != null && telephonyManager.getDeviceId() != null ?
                    telephonyManager.getDeviceId() : Settings.Secure.getString(EAnalytics.getContext()
                    .getContentResolver(), Settings.Secure.ANDROID_ID));
            String packageName = EAnalytics.getContext().getApplicationInfo().packageName;
            JSONUtils.put(internals, KEY_URL, "http://" + packageName);
            JSONUtils.put(internals, KEY_APPNAME, packageName);
            JSONUtils.put(internals, KEY_ADINFO_IS_LAT, String.valueOf(EAnalytics.sAdInfoIsLAT));
            JSONUtils.put(internals, KEY_ADINFO_ID, EAnalytics.sAdInfoId);
            JSONUtils.put(internals, KEY_EPOCH, String.valueOf(System.currentTimeMillis() / 1000));
        }

        public T set(String key, String value) {
            JSONUtils.put(properties, key, value);
            return (T) this;
        }

        public T setPageLocation(double latitude, double longitude) {
            JSONUtils.put(pages, KEY_PAGE_LATITUDE, String.valueOf(latitude));
            JSONUtils.put(pages, KEY_PAGE_LONGITUDE, String.valueOf(longitude));
            return (T) this;
        }

        private T setPagePath(String path) {
            JSONUtils.put(pages, KEY_PAGE_PATH, path);
            return (T) this;
        }

        private T setPageNewCustomer(int newCustomer) {
            JSONUtils.put(pages, KEY_PAGE_NEW_CUSTOMER, String.valueOf(newCustomer));
            return (T) this;
        }

        public T setPageEmail(String email) {
            JSONUtils.put(pages, KEY_PAGE_EMAIL, email);
            return (T) this;
        }

        public T setPageUid(String uid) {
            JSONUtils.put(pages, KEY_PAGE_UID, uid);
            return (T) this;
        }

        public T setPageProfile(String profile) {
            JSONUtils.put(pages, KEY_PAGE_PROFILE, profile);
            return (T) this;
        }

        public T setPageGroup(String group) {
            JSONUtils.put(pages, KEY_PAGE_GROUP, group);
            return (T) this;
        }

        public T setAction(EAAction action) {
            JSONUtils.put(pages, KEY_PAGE_ACTION, action.getJSONObject());
            return (T) this;
        }

        public T setProperty(EASiteCentricProperty property) {
            JSONUtils.put(pages, KEY_PAGE_PROPERTY, property.getJSONObject());
            return (T) this;
        }

        public EAProperties build() {
            EAProperties res = new EAProperties(this);
            return res;
        }
    }
}
