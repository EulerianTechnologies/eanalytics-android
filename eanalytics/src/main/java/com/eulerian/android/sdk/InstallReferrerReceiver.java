package com.eulerian.android.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Francois Rouault on 09/03/2015.
 * BroadcastReceiver for automatically storing Google Play Store referrer information as Eulerian Properties.
 * <p/>
 * <p>You can use InstallReferrerReceiver to capture and store referrer information,
 * and use that information to track how users from different sources are using your app.
 * To enable InstallReferrerReceiver, add a clause like the following
 * to the &lt;application&gt; tag of your AndroidManifest.xml.</p>
 * <p/>
 * <pre>
 * {@code
 * <receiver android:name="com.eulerian.android.sdk.InstallReferrerReceiver"
 *           android:exported="true">
 *     <intent-filter>
 *         <action android:name="com.android.vending.INSTALL_REFERRER" />
 *     </intent-filter>
 * </receiver>
 * }
 * </pre>
 * <p>If you plan to use multiple install refer, please refer to this workaround: &nbsp;https://mixpanel
 * .com/help/questions/articles/how-can-i-use-multiple-install-trackers-with-the-android-library&nbsp;</p>
 * <p/>
 * <p>Once you've added the &lt;receiver&gt; tag to your manifest,
 * the first call to {@link com.eulerian.android.sdk.EAnalytics#track(EAProperties)}
 * will include the user's Google Play Referrer as metadata. In addition, if
 * you include utm parameters in your link to Google Play, they will be parsed and
 * provided as individual properties in your track calls.</p>
 * <p/>
 * <p>InstallReferrerReceiver looks for any of the following parameters. All are optional.</p>
 * <ul>
 * <li>utm_source: often represents the source of your traffic (for example, a search engine or an ad)</li>
 * <li>utm_medium: indicates whether the link was sent via email, on facebook, or pay per click</li>
 * <li>utm_term: indicates the keyword or search term associated with the link</li>
 * <li>utm_content: indicates the particular content associated with the link (for example,
 * which email message was sent)</li>
 * <li>utm_campaign: the name of the marketing campaign associated with the link.</li>
 * </ul>
 * <p/>
 * <p>Whether or not the utm parameters are present, the InstallReferrerReceiver will
 * also create a "referrer" super property with the complete referrer string.</p>
 */
public class InstallReferrerReceiver extends BroadcastReceiver {
    private static final String TAG = InstallReferrerReceiver.class.getName();

    //test it using:
    // adb shell
    /*
    am broadcast -a com.android.vending.INSTALL_REFERRER -n com.eulerian.android.demo/com.eulerian.android.sdk
    .InstallReferrerReceiver --es "referrer"
    "utm_source=test_source&utm_medium=test_medium&utm_term=test_term&utm_content=te​st_content&utm_campaign=test_name"
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle extras = intent.getExtras();
        if (null == extras) {
            return;
        }
        final String referrer = extras.getString("referrer");
        if (null == referrer) {
            return;
        }

        final Map<String, String> newPrefs = new HashMap<>();
        newPrefs.put(PersistentIdentity.KEY_REFERRER, referrer);
        EAnalytics.sInstallReferrer = referrer;

//        final Matcher sourceMatcher = UTM_SOURCE_PATTERN.matcher(referrer);
//        final String source = find(sourceMatcher);
//        if (null != source) {
//            newPrefs.put(PersistentIdentity.KEY_UTM_SOURCE, source);
//        }

//        final Matcher mediumMatcher = UTM_MEDIUM_PATTERN.matcher(referrer);
//        final String medium = find(mediumMatcher);
//        if (null != medium) {
//            newPrefs.put(PersistentIdentity.KEY_UTM_MEDIUM, medium);
//        }

//        final Matcher campaignMatcher = UTM_CAMPAIGN_PATTERN.matcher(referrer);
//        final String campaign = find(campaignMatcher);
//        if (null != campaign) {
//            newPrefs.put(PersistentIdentity.KEY_UTM_CAMPAIGN, campaign);
//        }

//        final Matcher contentMatcher = UTM_CONTENT_PATTERN.matcher(referrer);
//        final String content = find(contentMatcher);
//        if (null != content) {
//            newPrefs.put(PersistentIdentity.KEY_UTM_CONTENT, content);
//        }

//        final Matcher termMatcher = UTM_TERM_PATTERN.matcher(referrer);
//        final String term = find(termMatcher);
//        if (null != term) {
//            newPrefs.put(PersistentIdentity.KEY_UTM_TERM, term);
//        }

        EALog.d(TAG, "Install Referrer received: " + newPrefs.toString());

        PersistentIdentity.getInstance().save(newPrefs);
    }

    private String find(Matcher matcher) {
        if (matcher.find()) {
            final String encoded = matcher.group(2);
            if (null != encoded) {
                try {
                    return URLDecoder.decode(encoded, "UTF-8");
                } catch (final UnsupportedEncodingException e) {
                    EALog.e(TAG, "Could not decode a parameter into UTF-8");
                }
            }
        }
        return null;
    }

//    private final Pattern UTM_SOURCE_PATTERN = Pattern.compile("(^|&)utm_source=([^&#=]*)([#&]|$)");
//    private final Pattern UTM_MEDIUM_PATTERN = Pattern.compile("(^|&)utm_medium=([^&#=]*)([#&]|$)");
//    private final Pattern UTM_CAMPAIGN_PATTERN = Pattern.compile("(^|&)utm_campaign=([^&#=]*)([#&]|$)");
//    private final Pattern UTM_CONTENT_PATTERN = Pattern.compile("(^|&)utm_content=([^&#=]*)([#&]|$)");
//    private final Pattern UTM_TERM_PATTERN = Pattern.compile("(^|&)utm_term=([^&#=]*)([#&]|$)");
}
