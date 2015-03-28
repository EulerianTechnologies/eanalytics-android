package com.eulerian.android.sdk;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by Francois Rouault on 25/02/2015.
 */
class EALog {

    private static String TAG = EAnalytics.class.getSimpleName();
    protected static boolean LOG_ENABLED = false;

    public static void v(String msg) {
        Log.v(TAG, msg);
    }

    public static void d(String msg) {
        if (LOG_ENABLED) Log.d(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static boolean assertCondition(boolean condition, String msg) {
        if (condition) {
            return true;
        }
        if (isDebug()) {
            throw new IllegalStateException(msg);
        } else {
            EALog.e(msg);
            //bad thing will append.
            //TODO: see with Eulerian
            return false;
        }
    }

    public static boolean isDebug() {
        Context ctx = EAnalytics.getContext();
        try {
            Class<?> clazz = Class.forName(ctx.getPackageName() + ".BuildConfig");
            Field field = clazz.getField("DEBUG");
            return (boolean) field.get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
