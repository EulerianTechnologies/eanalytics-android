package com.eulerian.android.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;

/**
 * Created by Francois Rouault on 09/03/2015.
 */
class PersistentIdentity {

    private static final String INSTALL_REFERRER_HAS_ALREADY_BEEN_SENT_ONCE = "referrer_sent_v1_8_0";
    private static PersistentIdentity mInstance;
    private final SharedPreferences mSharedPreferences;

    static final String KEY_REFERRER = "referrer";
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
        return mSharedPreferences.getString(KEY_ADVERTISING_ID, null);
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

    private void writeEdits(final SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= 9) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public void saveInstallReferrer(@NonNull String referrer) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PersistentIdentity.KEY_REFERRER, referrer);
        writeEdits(editor);
    }

    public String getInstallReferrer() {
        return mSharedPreferences.getString(KEY_REFERRER, null);
    }

    boolean shouldFetchInstallReferrer() {
        boolean isInstallReferrerSent = mSharedPreferences.getBoolean(INSTALL_REFERRER_HAS_ALREADY_BEEN_SENT_ONCE, false);
        return !isInstallReferrerSent;
    }

    public boolean shouldSendInstallReferrer() {
        boolean isInstallReferrerSent = mSharedPreferences.getBoolean(INSTALL_REFERRER_HAS_ALREADY_BEEN_SENT_ONCE, false);
        boolean isInstallReferrerAvailable = getInstallReferrer() != null;
        return isInstallReferrerAvailable && !isInstallReferrerSent;
    }

    public void setInstallReferrerSent() {
        mSharedPreferences.edit()
                .putBoolean(INSTALL_REFERRER_HAS_ALREADY_BEEN_SENT_ONCE, true)
                .apply();
    }
}
