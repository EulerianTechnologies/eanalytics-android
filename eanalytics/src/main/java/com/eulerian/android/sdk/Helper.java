package com.eulerian.android.sdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Francois Rouault on 07/03/2015.
 */
class Helper {

    private static final String RT_DOMAIN_REGEX = "([-a-zA-Z0-9^\\p{L}\\p{C}\\u00a1-\\uffff@:%_\\+.~#?&//=]{2," +
            "256}){1}(\\.[a-z]{2,4}){1}(\\:[0-9]*)?(\\/[-a-zA-Z0-9\\u00a1-\\uffff\\(\\)@:%," +
            "_\\+.~#?&//=]*)?([-a-zA-Z0-9\\(\\)@:%,_\\+.~#?&//=]*)?";

    static boolean isPermissionGranted(Context context, String permission) {
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    static boolean isDomainValid(String domain) {
        final Matcher contentMatcher = Pattern.compile(RT_DOMAIN_REGEX).matcher(domain);
        return contentMatcher.find();
    }

    public static boolean isEmailValid(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static CharSequence toString(InputStream is) {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder(is.available());
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
