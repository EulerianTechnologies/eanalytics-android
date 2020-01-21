package com.eulerian.android.sdk;

import android.os.Handler;

import java.util.List;

/**
 * Created by Francois Rouault on 28/03/2015.
 */
class PropertiesTracker implements Runnable {

    private final Handler handler;
    private final EAProperties properties;

    PropertiesTracker(EAProperties properties, Handler ui) {
        this.properties = properties;
        this.handler = ui;
    }

    @Override
    public void run() {
        handler.removeMessages(EAnalytics.HANDLER_MESSAGE_RETRY);

        if (PersistentIdentity.getInstance().shouldSendInstallReferrer()) {
            String installReferrer = PersistentIdentity.getInstance().getInstallReferrer();
            EALog.d("Tracking properties for the first time, add install referrer to it");
            JSONUtils.put(properties.mProperties, EAProperties.KEY_INSTALL_REFERRER, installReferrer);
            PersistentIdentity.getInstance().setInstallReferrerSent();
        }

        String propertiesToString = properties.getJson().toString();
        // FOR TEST
        // reminder : change also max size in Config.java
//        propertiesToString = "{\"TEST\":" + (new Random().nextInt(20)) + "}";
        // END FOR TEST

        EALog.d("Tracking properties");

        if (!ConnectivityHelper.isConnected(EAnalytics.getContext())) {
            EALog.d("-> no network access. Properties is being stored and will be sent later.", true);
            FileHelper.appendLine(propertiesToString);
            handler.sendEmptyMessageDelayed(EAnalytics.HANDLER_MESSAGE_RETRY, Config.NO_INTERNET_RETRY_DELAY_MILLIS);
            return;
        }

        List<String> storedProperties = FileHelper.getLines();
        if (storedProperties.isEmpty()) {
            final boolean success = HttpHelper.postData("[" + propertiesToString + "]");
            if (!success) {
                EALog.d("-> synchronization failed. Will retry if no other pending track is found.");
                FileHelper.appendLine(propertiesToString);
                handler.sendEmptyMessageDelayed(EAnalytics.HANDLER_MESSAGE_RETRY,
                        Config.POST_FAILED_RETRY_DELAY_MILLIS);
            } else {
                EALog.d("-> properties tracked !", true);
            }
            return;
        }

        EALog.d("-> " + storedProperties.size() + " stored properties found, current properties added to history to " +
                "be sent with stored ones.");
        FileHelper.appendLine(propertiesToString);//we treat the current properties as a stored properties (history)

        new StoredPropertiesTracker(handler).run();
    }

}
