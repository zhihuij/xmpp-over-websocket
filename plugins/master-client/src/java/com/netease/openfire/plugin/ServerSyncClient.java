package com.netease.openfire.plugin;

import org.jboss.netty.bootstrap.ClientBootstrap;

import com.netease.xmpp.master.client.ClientConfigCache;
import com.netease.xmpp.master.client.SyncClient;
import com.netease.xmpp.master.event.EventDispatcher;
import com.netease.xmpp.master.event.EventType;

public class ServerSyncClient extends SyncClient {
    public ServerSyncClient(int clientType) {
        super(clientType);
    }

    public void registerCustomEvent(EventDispatcher eventDispatcher,
            ClientConfigCache clientConfig, ClientBootstrap bootstrap) {
        XMPPServerConnectionEventHandler serverConnectionEventHandler = new XMPPServerConnectionEventHandler(
                bootstrap, eventDispatcher);

        eventDispatcher.registerEvent(serverConnectionEventHandler, //
                EventType.CLIENT_SERVER_CONNECTED, //
                EventType.CLIENT_SERVER_INFO_ACCEPTED, //
                EventType.CLIENT_SERVER_DISCONNECTED, //
                EventType.CLIENT_SERVER_HEARTBEAT, //
                EventType.CLIENT_SERVER_HEARTBEAT_TIMOUT);
    }

}
