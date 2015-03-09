package com.eulerian.android.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Map;

/**
 * Created by Francois Rouault on 09/03/2015.
 */
class PersistentIdentity {

    private static PersistentIdentity mInstance;
    private final SharedPreferences mSharedPreferences;

    public static final String KEY_REFERRER = "referrer";
    public static final String KEY_UTM_MEDIUM = "utm_medium";
    public static final String KEY_UTM_CONTENT = "utm_content";
    public static final String KEY_UTM_TERM = "utm_term";
    public static final String KEY_UTM_CAMPAIGN = "utm_campaign";
    public static final String KEY_UTM_SOURCE = "utm_source";

    public static PersistentIdentity getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PersistentIdentity(context);
        }
        return mInstance;
    }

    private PersistentIdentity(Context context) {
        mSharedPreferences = context.getSharedPreferences("eanalytics-prefs", Context.MODE_PRIVATE);
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

}
