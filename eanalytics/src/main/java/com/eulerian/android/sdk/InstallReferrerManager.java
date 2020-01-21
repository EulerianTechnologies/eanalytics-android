package com.eulerian.android.sdk;

import android.content.Context;
import android.os.RemoteException;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

/**
 * Reference: https://developer.android.com/google/play/installreferrer/library.html
 * <p>
 * Possible of improvement:
 * The install referrer information will be available for 90 days and won't change unless
 * the application is reinstalled. To avoid unnecessary API calls in your app, you should invoke
 * the API only once during the first execution after install.
 */
class InstallReferrerManager {

    static void startConnection(Context context) {
        final InstallReferrerClient referrerClient = InstallReferrerClient.newBuilder(context).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established.
                        EALog.d("Install referrer setup is OK.");
                        try {
                            ReferrerDetails response = referrerClient.getInstallReferrer();
                            String referrerUrl = response.getInstallReferrer();
                            PersistentIdentity.getInstance().saveInstallReferrer(referrerUrl);
                        } catch (RemoteException ignored) {
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        EALog.d("Install referrer is not supported.");
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        EALog.d("Install referrer: service is unavailable.");
                        // Connection couldn't be established.
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }
}
