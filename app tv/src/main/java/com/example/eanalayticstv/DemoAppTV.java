package com.example.eanalayticstv;

import android.app.Application;

import com.eulerian.android.sdk.EAnalytics;

/**
 * Created by François Rouault on 13/03/2017.
 */
public class DemoAppTV extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EAnalytics.init(this, "ett.eulerian.net", true);
    }
}
