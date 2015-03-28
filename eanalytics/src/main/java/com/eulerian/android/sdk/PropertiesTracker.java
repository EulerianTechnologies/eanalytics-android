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

        String propertiesToString = properties.getJson(true).toString();
        // FOR TEST
        // reminder : change also max size in Config.java
//        propertiesToString = "{\"TEST\":" + (new Random().nextInt(20)) + "}";
        // END FOR TEST

        EALog.v("Tracking properties : " + propertiesToString);

        if (!ConnectivityHelper.isConnected(EAnalytics.getContext())) {
            EALog.v("-> abort because no network access.");
            FileHelper.appendLine(propertiesToString);
            handler.sendEmptyMessageDelayed(EAnalytics.HANDLER_MESSAGE_RETRY, Config.NO_INTERNET_RETRY_DELAY_MILLIS);
            return;
        }

        List<String> storedProperties = FileHelper.getLines();
        if (storedProperties.isEmpty()) {
            final boolean success = HttpHelper.postData("[" + propertiesToString + "]");
            if (!success) {
                EALog.v("-> synchronization failed.");
                FileHelper.appendLine(propertiesToString);
                handler.sendEmptyMessageDelayed(EAnalytics.HANDLER_MESSAGE_RETRY, Config.RETRY_DELAY_MILLIS);
            } else {
                EALog.v("-> synchronization succeeded.");
            }
            return;
        }

        EALog.v("-> " + storedProperties.size() + " stored properties found, current properties added to history");
        FileHelper.appendLine(propertiesToString);//we treat the current properties as a stored properties (history)

        new StoredPropertiesTracker(handler).run();
    }

}
