package com.eulerian.android.sdk;

import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.UI_MODE_SERVICE;

/**
 * Created by Francois Rouault on 07/03/2015.
 */
class Helper {

    private static final String HOST_REGEX = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*" +
            "([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";

    static boolean isPermissionGranted(Context context, String permission) {
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    static boolean isHostValid(String domain) {
        final Matcher contentMatcher = Pattern.compile(HOST_REGEX).matcher(domain);
        return contentMatcher.find();
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

    public static boolean androidTV(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2
                && (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);
    }
}
