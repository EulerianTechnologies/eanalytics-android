package com.eulerian.android.sdk;

/**
* Created by Francois Rouault on 12/03/2015.
*/
public enum CurrencyISO {
    USD("USD"),
    GBP("GBP"),
    EUR("EUR");
    final String value;

    CurrencyISO(String s) {
        this.value = s;
    }
}
