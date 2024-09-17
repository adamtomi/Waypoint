package com.tomushimano.waypoint.util;

public class DontInvokeMe extends UnsupportedOperationException {

    public DontInvokeMe() {
        super("This constructor is private as it's not meant to be invoked.");
    }
}
