package com.eulerian.android.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Map;

/**
 * Created by Francois Rouault on 09/03/2015.
 */
class PersistentIdentity {

    private static final String INSTALL_REFERRER_HAS_ALREADY_BEEN_SENT_ONCE = "referrer_sent";
    private static PersistentIdentity mInstance;
    private final SharedPreferences mSharedPreferences;

    static final String KEY_REFERRER = "referrer";
    static final String KEY_UTM_MEDIUM = "utm_medium";
    static final String KEY_UTM_CONTENT = "utm_content";
    static final String KEY_UTM_TERM = "utm_term";
    static final String KEY_UTM_CAMPAIGN = "utm_campaign";
    static final String KEY_UTM_SOURCE = "utm_source";
    static final String KEY_ADVERTISING_ID = "id";
    static final String KEY_ADVERTISING_IS_LAT = "isLAT";

    public static PersistentIdentity getInstance() {
        if (mInstance == null) {
            mInstance = new PersistentIdentity();
        }
        return mInstance;
    }

    private PersistentIdentity() {
        mSharedPreferences = EAnalytics.getContext().getSharedPreferences("eanalytics-prefs", Context.MODE_PRIVATE);
    }

    public String getAdvertisingId() {
        return mSharedPreferences.getString(KEY_ADVERTISING_ID, "unknown");
    }

    public boolean getAdvertisingIsLat() {
        return mSharedPreferences.getBoolean(KEY_ADVERTISING_IS_LAT, false);
    }

    public synchronized void saveAdvertisingId(String id, boolean isLAT) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_ADVERTISING_ID, id);
        editor.putBoolean(KEY_ADVERTISING_IS_LAT, isLAT);
        writeEdits(editor);
    }

    public synchronized void save(Map<String, String> map) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (String key : map.keySet()) {
            editor.putString(key, map.get(key));
        }
        writeEdits(editor);
    }

    private void writeEdits(final SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= 9) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public String getInstallReferrer() {
        return mSharedPreferences.getString(KEY_REFERRER, null);
    }

    public boolean shouldSendInstallReferrer() {
        return mSharedPreferences.getBoolean(INSTALL_REFERRER_HAS_ALREADY_BEEN_SENT_ONCE, false)
                && getInstallReferrer() != null;
    }

    public void setInstallReferrerSent() {
        mSharedPreferences.edit()
                .putBoolean(INSTALL_REFERRER_HAS_ALREADY_BEEN_SENT_ONCE, true)
                .putString(KEY_REFERRER, null)
                .apply();
    }
}
