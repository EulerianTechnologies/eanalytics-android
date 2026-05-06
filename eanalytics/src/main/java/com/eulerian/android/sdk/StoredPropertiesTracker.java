package com.eulerian.android.sdk;

import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
            EALog.d("-> no network access. Properties is being stored and will be sent later.", true);
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
            handler.sendEmptyMessageDelayed(EAnalytics.HANDLER_MESSAGE_RETRY, Config.POST_FAILED_RETRY_DELAY_MILLIS);
        } else {
            EALog.d("-> all properties tracked successfully. Stored properties is now empty.");
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
                EAProperties eaValue = restoreEaProperties(line);
                boolean success = false;
                if (eaValue instanceof EATpClick || eaValue instanceof EATpView) {
                    if (counter == storedProperties.size() - 1 // last item
                            || (jsonArray.toString().getBytes().length + storedProperties.get(counter + 1)
                            .getBytes().length) > Config.MAX_UNZIPPED_BYTES_PER_SEND) {
                        // no more data OR json array is becoming too big -> send it
                        success = HttpHelper.getData(eaValue);
                    }
                } else {
                    JSONObject lineJson = new JSONObject(line);
                    jsonArray.put(lineJson);
                    if (counter == storedProperties.size() - 1 // last item
                            || (jsonArray.toString().getBytes().length + storedProperties.get(counter + 1)
                            .getBytes().length) > Config.MAX_UNZIPPED_BYTES_PER_SEND) {
                        // no more data OR json array is becoming too big -> send it
                        success = HttpHelper.postData(jsonArray.toString());
                    }
                }

                if (success) {
                    FileHelper.deleteLines(jsonArray.length());
                    jsonArray = new JSONArray(); // re-init in case the is still pending data.
                    EALog.d("-> properties tracked !", true);
                } else {
                    // something went wrong, will try on track() next call. This avoid infinite loop.
                    EALog.d("-> synchronization failed. Will retry later.");
                    return -1;
                }
            } catch (JSONException | UnsupportedEncodingException e) {
                EALog.e("Failed to JSON encode properties : " + line + ". Exception: " + e);
            }
            counter++;
        } while (counter < storedProperties.size());
        return counter;
    }

    public static EAProperties restoreEaProperties(String storedProperties) {
        try {
            JSONObject data = new JSONObject(storedProperties);

            String type = data.optString("type", null);

            if ("EATpView".equals(type)) {
                return EATpView.fromRawData(jsonToMap(data));

            } else if ("EATpClick".equals(type)) {
                return EATpClick.fromRawData(jsonToMap(data));
            }

            String path = data.optString("path", "");
            return new EAProperties(new EAProperties.Builder(path));

        } catch (JSONException e) {
            EALog.e("restoreEaProperties failed: " + e);
            return null;
        }
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = json.get(key);

            if (value instanceof JSONArray) {
                value = jsonArrayToList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }

            map.put(key, value);
        }

        return map;
    }

    public static List<Object> jsonArrayToList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);

            if (value instanceof JSONArray) {
                value = jsonArrayToList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }

            list.add(value);
        }

        return list;
    }
}
