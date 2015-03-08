package com.eulerian.android.sdk;


import android.Manifest;
import android.content.Context;

import com.eulerian.android.sdk.model.EAProperties;

/**
 * Created by Francois Rouault on 25/02/2015.
 */
public class EAnalytics {

    private static String sRTDomain;
    private static EAnalytics sInstance;
    private static Context sAppContext;

    private EAnalytics() {
        //cannot be initialized anywhere else
    }

    public static EAnalytics getInstance() {
        if (sInstance == null) {
            sInstance = new EAnalytics();
        }
        return sInstance;
    }

    /**
     * Initialization.
     *
     * @param context  the app context
     * @param rtDomain the rt domain
     */
    public static void init(Context context, String rtDomain) {
        EALog.assertCondition(sAppContext == null && sRTDomain == null, "Init must be called only once.");
        EALog.assertCondition(Utils.isPermissionGranted(context, Manifest.permission.READ_PHONE_STATE),
                "Init failed : permission is missing. You must add permission " +
                android.Manifest.permission.READ_PHONE_STATE + " in your app Manifest.xml.");
        EALog.assertCondition(context != null, "Init failed : context is null. You must provide a valid context.");
        EALog.assertCondition(rtDomain != null, "Init failed : RT domain is null. You must provide a valid RT domain.");
        sAppContext = context;
        sRTDomain = rtDomain;
    }

    public static Context getContext() {
        EALog.assertCondition(sAppContext != null, "The SDK has not been initialized. You must call EAnalytics.init" +
                "(Context, String) once.");
        return sAppContext;
    }

    public static void setLogEnabled(boolean enable) {
        EALog.LOG_ENABLED = enable;
        EALog.d("Log enabled");
    }

    public void track(EAProperties properties) {
        //TODO: check rt domain != null before sending request
        EALog.i("Tracking properties: " + properties.toJson(true));
    }

}
