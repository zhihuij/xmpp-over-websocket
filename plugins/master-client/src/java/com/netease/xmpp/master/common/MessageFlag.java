package com.netease.xmpp.master.common;

/**
 * Message flag.
 * 
 * @author jiaozhihui@corp.netease.com
 */
public class MessageFlag {
    public static final byte FLAG_HEATBEAT = 0x00;

    public static final byte FLAG_SERVER_UPDATED = 0x01;
    public static final byte FLAG_HASH_UPDATED = 0x02;

    public static final byte FLAG_HASH_ALL_COMPLETE = 0x0e;
    public static final byte FLAG_SERVER_ALL_COMPLETE = 0x0f;

    public static final byte FLAG_SERVER_UPDATE_COMPLETE = 0x11;
    public static final byte FLAG_HASH_UPDATE_COMPLETE = 0x12;
    
    public static final byte FLAG_SERVER_INFO = 0x21;
    public static final byte FLAG_SERVER_INFO_ACCEPTED = 0x21;
}
