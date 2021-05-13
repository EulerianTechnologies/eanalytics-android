package com.eulerian.android.demotv;

import android.app.Application;

import com.eulerian.android.sdk.Action;
import com.eulerian.android.sdk.EAProperties;
import com.eulerian.android.sdk.EAnalytics;
import com.eulerian.android.sdk.SiteCentricProperty;

/**
 * Created by François Rouault on 13/03/2017.
 */
public class DemoAppTV extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EAnalytics.init(this, "ett.eulerian.net", true);

        EAProperties properties = new EAProperties.Builder("the_path")
                .setNewCustomer(true)
                .setEmail("test-email")
                .setPageGroup("test-group")
                .setLocation(2.433, 43.2)
                .setProfile("test-profile")
                .setUID("test-uid")
                .set("whatever", "...")
                .set("whatever1", "...")
                .set("whatever2", "...")
                .setAction(new Action.Builder()
                        .setReference("test-ref-\"fefds$432`^")
                        .setIn("in-test")
                        .addOut(new String[]{"tata", "tutu", "tete"})
                        .build())
                .setProperty(new SiteCentricProperty.Builder()
                        .set("cle1", new String[]{"poisson", "viande"})
                        .set("cle2", "choucroute")
                        .build())
                .build();
        EAnalytics.getInstance().track(properties);
    }
}
