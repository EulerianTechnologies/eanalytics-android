package com.eulerian.android.demo;

import android.app.Application;

import com.eulerian.android.sdk.EAnalytics;

/**
 * Created by Francois Rouault on 03/03/2015.
 */
public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EAnalytics.init(this, "monRtDomain");
        EAnalytics.setLogEnabled(true);
    }
}
