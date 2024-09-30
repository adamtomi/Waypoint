package com.tomushimano.waypoint.util;

import java.io.Serial;

public class DontInvokeMe extends UnsupportedOperationException {
    @Serial
    private static final long serialVersionUID = -8745612180694336052L;

    public DontInvokeMe() {
        super("This constructor is private as it's not meant to be invoked.");
    }
}
