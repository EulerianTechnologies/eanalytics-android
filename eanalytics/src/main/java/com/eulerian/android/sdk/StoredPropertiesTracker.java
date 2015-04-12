package com.eulerian.android.sdk;

import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Francois Rouault on 28/03/2015.
 */
public class StoredPropertiesTracker implements Runnable {

    private final Handler handler;

    public StoredPropertiesTracker(Handler ui) {
        this.handler = ui;
    }

    @Override
    public void run() {
        EALog.d("Tracking stored properties");
        handler.removeMessages(EAnalytics.HANDLER_MESSAGE_RETRY);

        if (!ConnectivityHelper.isConnected(EAnalytics.getContext())) {
            EALog.d("-> abort because no network access.");
            handler.sendEmptyMessageDelayed(EAnalytics.HANDLER_MESSAGE_RETRY, Config.NO_INTERNET_RETRY_DELAY_MILLIS);
            return;
        }

        List<String> storedProperties = FileHelper.getLines();
        if (storedProperties.isEmpty()) {
            EALog.d("-> no properties stored.");
            return;
        }

        EALog.d("-> " + storedProperties.size() + " stored properties found. Added for synchronization.");
        int result = postStoredProperties(storedProperties);
        if (result == -1) {
            handler.sendEmptyMessageDelayed(EAnalytics.HANDLER_MESSAGE_RETRY, Config.RETRY_DELAY_MILLIS);
        } else {
            EALog.d("-> properties + history = " + result + " synchronization succeeded.");
        }
    }

    /**
     * @param storedProperties
     * @return the number of properties that have been synchronized. -1 otherwise
     */
    public static int postStoredProperties(List<String> storedProperties) {
        int counter = 0;
        JSONArray jsonArray = new JSONArray();
        do {
            String line = storedProperties.get(counter);
            try {
                JSONObject lineJson = new JSONObject(line);
                jsonArray.put(lineJson);
                if (counter == storedProperties.size() - 1 // last item
                        || (jsonArray.toString().getBytes().length + storedProperties.get(counter + 1)
                        .getBytes().length) > Config.MAX_UNZIPPED_BYTES_PER_SEND) {
                    // no more data OR json array is becoming too big -> send it
                    boolean success = HttpHelper.postData(jsonArray.toString());
                    if (success) {
                        FileHelper.deleteLines(jsonArray.length());
                        jsonArray = new JSONArray(); // re-init in case the is still pending data.
                    } else {
                        // something went wrong, will try on next call to track(). This avoid infinite loop.
                        EALog.d("-> synchronization failed.");
                        return -1;
                    }
                }
            } catch (JSONException e) {
                EALog.e("Failed to JSON encode properties : " + line + ". Exception: " + e);
            }
            counter++;
        } while (counter < storedProperties.size());
        return counter;
    }
}
