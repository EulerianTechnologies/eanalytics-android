package com.eulerian.android.sdk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONObject;

/**
 * Created by Francois Rouault on 07/03/2015.
 */
public class EAProperties {

    //- internal keys
    static final String KEY_INSTALL_REFERRER = "ea-android-referrer";
    private static final String KEY_EOS = "eos";
    private static final String KEY_EHW = "ehw";
    private static final String KEY_ADINFO_IS_LAT = "ea-android-islat";
    private static final String KEY_EUIDL = "euidl";
    private static final String KEY_URL = "url";
    private static final String KEY_APPNAME = "ea-appname";
    private static final String KEY_ADINFO_ID = "ea-android-adid";
    private static final String KEY_EPOCH = "ereplay-time";
    private static final String KEY_APP_VERSION_NAME = "ea-appversion";
    private static final String KEY_APP_VERSION_CODE = "ea-appversionbuild";
    private static final String KEY_IS_TV = "ea-apptv";

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
    private static final String KEY_MAC = "ea-android-mac";
    private static final String KEY_SDK_VERSION = "ea-android-sdk-version";

    JSONObject mProperties;
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

    protected JSONObject getJson() {
        return JSONUtils.merge(mInternals, mPages, mProperties);
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
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            JSONUtils.put(pages, KEY_PAGE_PATH, path);
        }

        private void initInternalParams() {
            JSONUtils.put(internals, KEY_EOS, "Android" + Build.VERSION.RELEASE);
            JSONUtils.put(internals, KEY_EHW, Build.MANUFACTURER + " " + Build.MODEL);
            JSONUtils.put(internals, KEY_EUIDL, EAnalytics.getEuidl());
            JSONUtils.put(internals, KEY_MAC, getMacAddress());
            String packageName = EAnalytics.getContext().getApplicationInfo().packageName;
            JSONUtils.put(internals, KEY_URL, "http://" + packageName);
            JSONUtils.put(internals, KEY_APPNAME, packageName);
            JSONUtils.put(internals, KEY_ADINFO_IS_LAT, String.valueOf(EAnalytics.sAdInfoIsLAT));
            JSONUtils.put(internals, KEY_ADINFO_ID, EAnalytics.sAdInfoId);
            JSONUtils.put(internals, KEY_EPOCH, String.valueOf(System.currentTimeMillis() / 1000));
            JSONUtils.put(internals, KEY_SDK_VERSION, BuildConfig.VERSION_NAME);
            setVersionCodeAndVersionName();
            JSONUtils.put(internals, KEY_IS_TV, Helper.androidTV(EAnalytics.getContext()) ? 1 : 0);
        }

        private void setVersionCodeAndVersionName() {
            Context context = EAnalytics.getContext();
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                JSONUtils.put(internals, KEY_APP_VERSION_NAME, packageInfo.versionName);
                JSONUtils.put(internals, KEY_APP_VERSION_CODE, String.valueOf(packageInfo.versionCode));
            } catch (PackageManager.NameNotFoundException e) {
                EALog.e("Error while getting package manager / package info. Error : " +
                        (e == null ? "null" : e.getMessage()));
            }
        }

        @Nullable
        private String getMacAddress() {
            WifiManager wifiManager = (WifiManager) EAnalytics.getContext().getSystemService(Context.WIFI_SERVICE);
            return wifiManager != null && wifiManager.getConnectionInfo() != null ?
                    wifiManager.getConnectionInfo().getMacAddress() :
                    null;
        }

        public T set(String key, String value) {
            JSONUtils.put(properties, key, value);
            return (T) this;
        }

        protected T set(String key, int value) {
            JSONUtils.put(properties, key, value);
            return (T) this;
        }

        public T setLocation(double latitude, double longitude) {
            JSONUtils.put(pages, KEY_PAGE_LATITUDE, String.valueOf(latitude));
            JSONUtils.put(pages, KEY_PAGE_LONGITUDE, String.valueOf(longitude));
            return (T) this;
        }

        public T setNewCustomer(boolean newCustomer) {
            if (newCustomer) {
                JSONUtils.put(pages, KEY_PAGE_NEW_CUSTOMER, String.valueOf(1));
            }
            return (T) this;
        }

        public T setEmail(String email) {
            if (TextUtils.isEmpty(email)) {
                EALog.w("Email is empty");
            }
            JSONUtils.put(pages, KEY_PAGE_EMAIL, email);
            return (T) this;
        }

        public T setUID(String uid) {
            if (!TextUtils.isEmpty(uid)) {
                JSONUtils.put(pages, KEY_PAGE_UID, uid);
            } else {
                EALog.w("UID is empty, should not be.");
            }
            return (T) this;
        }

        public T setProfile(String profile) {
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
