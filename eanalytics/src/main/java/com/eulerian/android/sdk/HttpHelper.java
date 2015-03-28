package com.eulerian.android.sdk;

import org.apache.http.HttpVersion;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Francois Rouault on 15/03/2015.
 */
class HttpHelper {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String GZIP = "gzip";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String MIME_FORM_ENCODED = "application/x-www-form-urlencoded";

    // Establish client once, as static field with static setup block.
    // (This is a best practice in HttpClient docs - but will leave reference until *process* stopped on Android.)
    private static final DefaultHttpClient client;

    static {
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
        params.setParameter(CoreProtocolPNames.USER_AGENT, "Apache-HttpClient/Android");
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        params.setParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
        client = new DefaultHttpClient(params);
    }


    public static boolean postData(String value) {
        try {
            EALog.v("-> post data, synchronizing... " + value);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Random().nextInt(4) != 0;
//        try {
//            URL url = new URL(EAnalytics.sRTDomain);
//            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//            conn.addRequestProperty(ACCEPT_ENCODING, GZIP);
//            conn.addRequestProperty(CONTENT_TYPE, MIME_FORM_ENCODED);
//            conn.setReadTimeout(10000);
//            conn.setConnectTimeout(15000);
//            conn.setRequestMethod("POST");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//
//            byte[] zippedProperties = getGZippedBytes(value.getJson(true).toString());
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(zippedProperties.length);
//
//            OutputStream os = conn.getOutputStream();
//            byteArrayOutputStream.writeTo(os);
//            os.close();
//
//            conn.connect();
//
//            return conn.getResponseCode() == 200;
//        } catch (IOException e) {
//            EALog.e(TAG, "postData failed : " + e);
//        }
//        return false;
    }

//    public static boolean postData(EAProperties properties) {
//
//        EALog.assertCondition(TAG, Utils.isDomainValid(EAnalytics.sRTDomain), "You must call EAnalytics.init() once
// " +
//                "before using it. In your Application.onCreate() for instance.");
//
//        HttpPost httpPost = new HttpPost(EAnalytics.sRTDomain);
//
//        try {
//            httpPost.setHeader(ACCEPT_ENCODING, GZIP);
//            httpPost.setHeader(CONTENT_TYPE, MIME_FORM_ENCODED);
//
//            byte[] zippedProperties = getGZippedBytes(properties.getJson(true).toString());
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(zippedProperties.length);
//            byteArrayOutputStream.write(zippedProperties);
//
//            httpPost.setEntity(new ByteArrayEntity(zippedProperties));
//
//            // Execute HTTP Post Request
//            HttpResponse response = client.execute(httpPost);
//
//            return response.getStatusLine().getStatusCode() == 200;
//
//        } catch (ClientProtocolException e) {
//            EALog.e(TAG, "postData failed : " + e);
//        } catch (IOException e) {
//            EALog.e(TAG, "postData failed : " + e);
//        }
//        return false;
//    }

    private static byte[] getGZippedBytes(String value) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = null;
        try {
            gzos = new GZIPOutputStream(baos);
            gzos.write(value.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzos != null) try {
                gzos.close();
            } catch (IOException ignore) {
            }
        }
        return baos.toByteArray();
    }

}
