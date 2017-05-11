package com.eulerian.android.sdk;

/**
 * Created by François Rouault on 11/05/2017.
 */
public class EulerianException extends RuntimeException {

    public EulerianException(Throwable cause) {
        super(cause == null ? "null" : cause.getMessage());
    }

    public EulerianException(String message) {
        super(wrapMessage(message));
    }

    static String wrapMessage(String message) {
        String res = "\n------------------------------------------------";
        res += "\n|                 EULERIAN                     |";
        res += "\n------------------------------------------------";
        res += "\n" + message;
        res += "\n------------------------------------------------";
        return res;
    }
}
