package com.netease.xmpp.master.client;

import com.netease.xmpp.master.common.ConfigCache;

public class ClientConfigCache extends ConfigCache {
    private static ClientConfigCache instance = null;

    public static ClientConfigCache getInstance() {
        if (instance == null) {
            instance = new ClientConfigCache();
        }

        return instance;
    }
    
    private String masterServerHost = null;
    private int masterServerPort = 0;
    
    private ClientConfigCache() {
        // Do nothing
    }

    public String getMasterServerHost() {
        return masterServerHost;
    }

    public void setMasterServerHost(String masterServerHost) {
        this.masterServerHost = masterServerHost;
    }

    public int getMasterServerPort() {
        return masterServerPort;
    }

    public void setMasterServerPort(int masterServerPort) {
        this.masterServerPort = masterServerPort;
    }
}
