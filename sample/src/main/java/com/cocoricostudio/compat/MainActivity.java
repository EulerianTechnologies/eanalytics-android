package com.cocoricostudio.compat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.eulerian.android.sdk.EAnalytics;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EAnalytics.init(this, "ett.eulerian.net", true);
    }
}
