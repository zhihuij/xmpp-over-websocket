package com.netease.xmpp.master.event;

public enum EventType {
    // ==================CLIENT=====================//
    /**
     * Client disconnected from master server.
     */
    CLIENT_SERVER_DISCONNECTED,
    /**
     * Client connected with master server.
     */
    CLIENT_SERVER_CONNECTED,

    /**
     * Heart beat from master server.
     */
    CLIENT_SERVER_HEARTBEAT,
    /**
     * Heart beat timeout.
     */
    CLIENT_SERVER_HEARTBEAT_TIMOUT,
    
    /**
     * Server info accepted.
     */
    CLIENT_SERVER_INFO_ACCEPTED
}
