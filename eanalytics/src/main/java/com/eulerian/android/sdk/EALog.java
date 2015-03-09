package com.eulerian.android.sdk;

import android.util.Log;

/**
 * Created by Francois Rouault on 25/02/2015.
 */
public class EALog {

    protected static boolean LOG_ENABLED = false;

    public static void d(String tag, String msg) {
        if (LOG_ENABLED) Log.d(tag, msg);
    }

    public static void assertCondition(String tag, boolean condition, String msg) {
        if (condition) {
            return;
        }
        EALog.e(tag, msg);
        throw new IllegalStateException(msg);
    }

    public static void e(String tag, String msg) {
        if (LOG_ENABLED) Log.e(tag, msg);
    }
}
