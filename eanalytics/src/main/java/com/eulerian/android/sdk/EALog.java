package com.eulerian.android.sdk;

import android.util.Log;

/**
 * Created by Francois Rouault on 25/02/2015.
 */
class EALog {

    private static String TAG = EAnalytics.class.getSimpleName();
    protected static boolean LOG_ENABLED = false;

    public static void d(String msg) {
        if (LOG_ENABLED) Log.d(TAG, msg);
    }

    public static void assertCondition(boolean condition, String msg) {
        if (condition) {
            return;
        }
        EALog.e(msg);
        throw new IllegalStateException(msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }
}
