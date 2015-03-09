package com.eulerian.android.sdk;


import android.Manifest;
import android.content.Context;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Francois Rouault on 25/02/2015.
 */
public class EAnalytics {

    private static final String TAG = EAnalytics.class.getName();
    private static String sRTDomain;
    private static EAnalytics sInstance;
    private static Context sAppContext;
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    static String sAdInfoId = "undefined";
    static boolean sAdInfoIsLAT = false;

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
    public static void init(Context context, String rtDomain, boolean log) {
        EALog.LOG_ENABLED = log;
        EALog.assertCondition(TAG, sAppContext == null && sRTDomain == null, "Init must be called only once.");
        EALog.assertCondition(TAG, Utils.isPermissionGranted(context, Manifest.permission.READ_PHONE_STATE),
                "Init failed : permission is missing. You must add permission " +
                        android.Manifest.permission.READ_PHONE_STATE + " in your app Manifest.xml.");
        EALog.assertCondition(TAG, context != null, "Init failed : context is null. You must provide a valid context.");
        EALog.assertCondition(TAG, Utils.isDomainValid(rtDomain), "Init failed : " + rtDomain + " is not a valid RT " +
                "domain");
        sAppContext = context;
        sRTDomain = rtDomain;
        sAdInfoId = PersistentIdentity.getInstance().getAdvertisingId();
        sAdInfoIsLAT = PersistentIdentity.getInstance().getAdvertisingIsLat();
        EALog.d(TAG, "Initialized with " + rtDomain + " domain.");
        getInstance().mExecutor.execute(new GetAdInfo());
    }

    static Context getContext() {
        EALog.assertCondition(TAG, sAppContext != null, "The SDK has not been initialized. You must call EAnalytics" +
                ".init(Context, String) once.");
        return sAppContext;
    }

    public void track(final EAProperties properties) {
        //TODO: check rt domain != null before sending request
        mExecutor.execute(new PropertiesTracker(properties));
    }

    static class PropertiesTracker implements Runnable {

        private final EAProperties properties;

        PropertiesTracker(EAProperties properties) {
            this.properties = properties;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            EALog.d(TAG, "Tracking properties: " + properties.toJson(true));
        }
    }

    static class GetAdInfo implements Runnable {
        private static final String TAG = GetAdInfo.class.getName();

        @Override
        public void run() {
            AdvertisingIdClient.Info adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
            } catch (IOException e) {
                EALog.e(TAG, "Unrecoverable error connecting to Google Play services (e.g. the old version of the " +
                        "service doesn't support getting AdvertisingId)");
            } catch (GooglePlayServicesRepairableException e) {
                EALog.e(TAG, "Google Play Services is not installed, up-to-date, or enabled");
            } catch (GooglePlayServicesNotAvailableException e) {
                EALog.e(TAG, "Google Play services is not available entirely.");
            }
            if (adInfo == null) {
                EALog.d(TAG, "AdvertisingIdClient null");
                return;
            }
            sAdInfoId = adInfo.getId();
            sAdInfoIsLAT = adInfo.isLimitAdTrackingEnabled();
            PersistentIdentity.getInstance().saveAdvertisingId(sAdInfoId, sAdInfoIsLAT);
            EALog.d(TAG, "AdvertisingIdClient id:" + sAdInfoId + ", isLat: " + sAdInfoIsLAT);
        }
    }

}
