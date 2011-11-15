package com.netease.xmpp.master.common;

/**
 * Message type for sending message between server and client.
 * 
 * @author jiaozhihui@corp.netease.com
 */
public class Message {
    /**
     * Message flag, {@link MessageFlag}
     */
    private byte flag;
    /**
     * Version for info.
     */
    private int version;
    /**
     * Data length.
     */
    private int dataLength;
    /**
     * Actual data.
     */
    private byte[] data;

    public Message(byte flag, int version, int dataLength, byte[] data) {
        this.flag = flag;
        this.version = version;
        this.dataLength = dataLength;
        this.data = data;
    }

    public byte getFlag() {
        return flag;
    }

    public int getVersion() {
        return version;
    }

    public int getDataLength() {
        return dataLength;
    }

    public byte[] getData() {
        return data;
    }

    /**
     * Get the actual message size.
     * 
     * @return the actual message size.
     */
    public int getMessageSize() {
        return dataLength + 4 + 4 + 1;
    }
}
