package com.netease.xmpp.master.event;

public class UnrecognizedEvent extends RuntimeException {
    private static final long serialVersionUID = 8201589380344746549L;

    public UnrecognizedEvent(String message) {
        super(message);
    }
}
