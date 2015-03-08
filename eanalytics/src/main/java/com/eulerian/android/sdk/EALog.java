package com.eulerian.android.sdk;

import android.util.Log;

/**
 * Created by Francois Rouault on 25/02/2015.
 */
public class EALog {

    protected static String TAG = EAnalytics.class.getName();
    protected static boolean LOG_ENABLED = false;

    public static void d(String msg) {
        if (LOG_ENABLED) Log.d(TAG, msg);
    }

    public static void i(String msg) {
        if (LOG_ENABLED) Log.i(TAG, msg);
    }

    public static void assertCondition(boolean condition, String msg) {
        if (condition) {
            return;
        }
        if (LOG_ENABLED) Log.e(TAG, msg);
        throw new IllegalStateException(msg);
    }
}
