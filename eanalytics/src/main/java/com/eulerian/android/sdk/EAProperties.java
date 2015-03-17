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
    private static final String KEY_INSTALL_REFERRER = "ea-android-referrer";
    private static final String KEY_EOS = "eos";
    private static final String KEY_EHW = "ehw";
    private static final String KEY_ADINFO_IS_LAT = "ea-android-islat";
    private static final String KEY_EUIDL = "euidl";
    private static final String KEY_URL = "url";
    private static final String KEY_APPNAME = "ea-appname";
    private static final String KEY_ADINFO_ID = "ea-android-adid";
    private static final String KEY_EPOCH = "ereplay-time";
    //- page keys
    private static final String KEY_PAGE_LATITUDE = "ea-lat";
    private static final String KEY_PAGE_LONGITUDE = "ea-lon";
    private static final String KEY_PAGE_PATH = "path";
    private static final String KEY_PAGE_EMAIL = "email";
    private static final String KEY_PAGE_UID = "uid";
    private static final String KEY_PAGE_PROFILE = "profile";
    private static final String KEY_PAGE_GROUP = "pagegroup";
    private static final String KEY_PAGE_ACTION = "action";
    private static final String KEY_PAGE_PROPERTY = "property";
    private static final String KEY_PAGE_NEW_CUSTOMER = "newcustomer";

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
    JSONObject getJson(boolean withInternals) {
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

        public Builder(String path) {
            initInternalParams();
            setPagePath(path);
        }

        private void initInternalParams() {
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

        protected T set(String key, int value) {
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

        public T setPageNewCustomer(boolean newCustomer) {
            if (newCustomer) {
                JSONUtils.put(pages, KEY_PAGE_NEW_CUSTOMER, String.valueOf(1));
            }
            return (T) this;
        }

        public T setPageEmail(String email) {
            if (!Utils.isEmailValid(email)) {
                EALog.w("Email \"" + email + "\" is not a valid email.");
            }
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

        public T setAction(Action action) {
            JSONUtils.put(pages, KEY_PAGE_ACTION, action.getJson());
            return (T) this;
        }

        public T setProperty(SiteCentricProperty property) {
            JSONUtils.put(pages, KEY_PAGE_PROPERTY, property.getJson());
            return (T) this;
        }

        public EAProperties build() {
            EAProperties res = new EAProperties(this);
            return res;
        }
    }
}
