package com.eulerian.android.sdk;


import android.Manifest;
import android.content.Context;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Francois Rouault on 25/02/2015.
 */
public class EAnalytics {

    static String sRTDomain;
    private static EAnalytics sInstance;
    private static Context sAppContext;
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    static String sAdInfoId;
    static String sInstallReferrer;
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
        EALog.assertCondition(sAppContext == null && sRTDomain == null, "Init must be called only once.");
        EALog.assertCondition(Utils.isPermissionGranted(context, Manifest.permission.READ_PHONE_STATE),
                "Init failed : permission is missing. You must add permission " +
                        android.Manifest.permission.READ_PHONE_STATE + " in your app Manifest.xml.");//TODO: confirm
        // with Eulerian whether throwing exception or just warn. Not good: TelephonyManager.getDeviceId
        // won't be accessible. Good: client don't have to add this permission if he don't want to.
        EALog.assertCondition(Utils.isPermissionGranted(context, Manifest.permission.ACCESS_NETWORK_STATE),
                "Init failed : permission is missing: Your must add permission " + Manifest.permission
                        .ACCESS_NETWORK_STATE + " in your app Manifest.xml"); // TODO: confirm assert with Eulerian
        EALog.assertCondition(context != null, "Init failed : context is null. You must provide a valid context.");
        EALog.assertCondition(Utils.isDomainValid(rtDomain), "Init failed : " + rtDomain + " is not a valid RT " +
                "domain");
        sAppContext = context;
        sRTDomain = rtDomain;
        sAdInfoId = PersistentIdentity.getInstance().getAdvertisingId();
        sAdInfoIsLAT = PersistentIdentity.getInstance().getAdvertisingIsLat();
        sInstallReferrer = PersistentIdentity.getInstance().getInstallReferrer();
        EALog.d("Eulerian Analytics initialized with " + rtDomain + " domain.");
        getInstance().mExecutor.execute(new GetAdInfo());
    }

    static Context getContext() {
        EALog.assertCondition(sAppContext != null, "The SDK has not been initialized. You must call EAnalytics" +
                ".init(Context, String) once.");
        return sAppContext;
    }

    public void track(final EAProperties properties) {
        EALog.assertCondition(sRTDomain != null, "The SDK has not been initialized. You must call EAnalytics" +
                ".init(Context, String) once.");
        mExecutor.execute(new PropertiesTracker(properties));
    }

    static class PropertiesTracker implements Runnable {

        private final EAProperties properties;

        PropertiesTracker(EAProperties properties) {
            this.properties = properties;
        }

        @Override
        public void run() {
            String propertiesToString = properties.getJson(true).toString();

            EALog.d("Tracking properties : " + propertiesToString);

            if (!ConnectivityHelper.isConnected(sAppContext)) {
                EALog.d("-> no network access. Will try later.");
                FileHelper.appendLine(propertiesToString);
                return;
            }

            List<String> storedProperties = FileHelper.getLines();
            if (storedProperties.isEmpty()) {
                boolean success = HttpHelper.postData("[" + propertiesToString + "]");
                if (!success) {
                    EALog.d("-> synchronization failed. Will try later.");
                    FileHelper.appendLine(propertiesToString);
                } else {
                    EALog.d("-> synchronization succeeded.");
                }
                return;
            }

            EALog.d("-> " + storedProperties.size() + " stored properties found. Added for synchronization.");
            FileHelper.appendLine(propertiesToString);
            storedProperties.add(propertiesToString);
            try {
                int counter = 0;
                JSONArray jsonArray = new JSONArray();
                do {
                    String line = storedProperties.get(counter);
                    JSONObject lineJson = new JSONObject(line);
                    jsonArray.put(lineJson);
                    if (counter == storedProperties.size() - 1 // last item
                            || (jsonArray.toString().getBytes().length + storedProperties.get(counter + 1)
                            .getBytes().length) > HttpHelper.MAX_UNZIPPED_BYTES_PER_SEND) {
                        // no more data OR json array is becoming too big -> send it
                        boolean success = HttpHelper.postData(jsonArray.toString());
                        if (success) {
                            FileHelper.deleteLines(jsonArray.length());
                            jsonArray = new JSONArray(); // re-init in case the is still pending data.
                        } else {
                            // something went wrong, will try on next call to track(). This avoid infinite loop.
                            EALog.d("-> synchronization failed. Will try later.");
                            return;
                        }
                    }
                    counter++;
                } while (counter < storedProperties.size());
                EALog.d("-> synchronization succeeded : " + counter + " EAProperties synchronized.");
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            EALog.d("AdvertisingIdClient id:" + sAdInfoId + ", isLat: " + sAdInfoIsLAT);
        }
    }
}