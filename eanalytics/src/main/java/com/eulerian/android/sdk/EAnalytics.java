package com.eulerian.android.sdk;


import android.Manifest;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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

    private static final String TAG = EAnalytics.class.getSimpleName();
    protected static final int HANDLER_MESSAGE_RETRY = 1;
    static String sRTDomain;
    private static EAnalytics sInstance;
    private static Context sAppContext;
    protected Executor mExecutor = Executors.newSingleThreadExecutor();
    static String sAdInfoId;
    static boolean sAdInfoIsLAT = false;

    protected Handler mUiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MESSAGE_RETRY:
                    EALog.d("Retry strategy");
                    EAnalytics.getInstance().track(null);
                    break;
            }
        }
    };

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
     * @param context the app context
     * @param host    the host
     */
    public static void init(Context context, String host, boolean log) {
        EALog.LOG_ENABLED = log;
        EALog.assertCondition(sAppContext == null && sRTDomain == null, "Init must be called only once.");
        EALog.assertCondition(Helper.isPermissionGranted(context, Manifest.permission.INTERNET),
                "Init failed : permission is missing. You must add permission " +
                        Manifest.permission.INTERNET + " in your app Manifest.xml.");
        EALog.assertCondition(Helper.isPermissionGranted(context, Manifest.permission.READ_PHONE_STATE),
                "Init failed : permission is missing. You must add permission " +
                        android.Manifest.permission.READ_PHONE_STATE + " in your app Manifest.xml.");
        // with Eulerian whether throwing exception or just warn. Not good: TelephonyManager.getDeviceId
        // won't be accessible. Good: client don't have to add this permission if he don't want to.
        EALog.assertCondition(Helper.isPermissionGranted(context, Manifest.permission.ACCESS_NETWORK_STATE),
                "Init failed : permission is missing: Your must add permission " + Manifest.permission
                        .ACCESS_NETWORK_STATE + " in your app Manifest.xml");
        EALog.assertCondition(Helper.isPermissionGranted(context, Manifest.permission.ACCESS_WIFI_STATE),
                "Init failed : permission is missing: Your must add permission " + Manifest.permission
                        .ACCESS_WIFI_STATE + " in your app Manifest.xml");
        EALog.assertCondition(context != null, "Init failed : context is null. You must provide a valid context.");
        EALog.assertCondition(Helper.isHostValid(host), "Init failed : " + host + " is not a valid host name. " +
                "For instance, test.example.net is a valid.");
        sAppContext = context;
        sRTDomain = "https://" + host + "/collectorjson/-/123";
        sAdInfoId = PersistentIdentity.getInstance().getAdvertisingId();
        sAdInfoIsLAT = PersistentIdentity.getInstance().getAdvertisingIsLat();
        EALog.d("Eulerian Analytics initialized with " + host);
        getInstance().mExecutor.execute(new GetAdInfo());
        getInstance().track(null);
    }

    static Context getContext() {
        EALog.assertCondition(sAppContext != null, "The SDK has not been initialized. You must call EAnalytics" +
                ".init(Context, String) once.");
        return sAppContext;
    }

    public void track(final EAProperties properties) {
        EALog.d("track");
        EALog.assertCondition(sRTDomain != null, "The SDK has not been initialized. You must call EAnalytics" +
                ".init(Context, String) once.");
        if (properties == null) {
            mExecutor.execute(new StoredPropertiesTracker(mUiHandler));
        } else {
            mExecutor.execute(new PropertiesTracker(properties, mUiHandler));
        }
    }

    static class GetAdInfo implements Runnable {
        @Override
        public void run() {
            AdvertisingIdClient.Info adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
            } catch (IOException e) {
                EALog.e("Unrecoverable error connecting to Google Play services (e.g. the old version of the service " +
                        "doesn't support getting AdvertisingId)");
            } catch (GooglePlayServicesRepairableException e) {
                EALog.e("Google Play Services is not installed, up-to-date, or enabled");
            } catch (GooglePlayServicesNotAvailableException e) {
                EALog.e("Google Play services is not available entirely.");
            }
            if (adInfo == null) {
                EALog.d("AdvertisingIdClient null");
                return;
            }
            sAdInfoId = adInfo.getId();
            sAdInfoIsLAT = adInfo.isLimitAdTrackingEnabled();
            PersistentIdentity.getInstance().saveAdvertisingId(sAdInfoId, sAdInfoIsLAT);
            EALog.d("AdvertisingIdClient id and isLat found");
        }
    }
}