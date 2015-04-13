package com.eulerian.android.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Francois Rouault on 15/03/2015.
 */
class HttpHelper {

    public static boolean postData(String value) {
        EALog.d("-> posting data");
        // FOR TEST
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return new Random().nextInt(4) != 0;
//        END FOR TEST
        try {
            URL url = new URL(EAnalytics.sRTDomain + (System.currentTimeMillis() / 1000));
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.addRequestProperty("Content-Encoding", "gzip");
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setReadTimeout(2000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(baos);
            gos.write(value.getBytes());
            gos.close();
            baos.writeTo(conn.getOutputStream());

            conn.connect();

            if (conn.getResponseCode() == 200) {
                CharSequence response = Helper.toString(conn.getInputStream());
                EALog.d("-> post response: " + response);
                JSONObject json = new JSONObject(response.toString());
                return !json.getBoolean("error");
            } else {
                return false;
            }
        } catch (IOException | JSONException e) {
            EALog.e("postData failed : " + e);
        }
        return false;
    }

}
