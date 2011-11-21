package com.netease.openfire.plugin;

import java.io.IOException;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jivesoftware.openfire.ConnectionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.util.cache.Cache;
import org.jivesoftware.util.cache.CacheFactory;

import com.netease.xmpp.master.common.ConfigConst;
import com.netease.xmpp.master.common.Message;
import com.netease.xmpp.master.common.MessageFlag;
import com.netease.xmpp.master.common.ServerListProtos.Server.ServerInfo;
import com.netease.xmpp.master.event.EventContext;
import com.netease.xmpp.master.event.EventDispatcher;
import com.netease.xmpp.master.event.EventType;
import com.netease.xmpp.master.event.client.ServerConnectionEventHandler;

public class XMPPServerConnectionEventHandler extends ServerConnectionEventHandler {
    private static final String STATUS_PLUGIN_NAME = "status";

    private static final String CACHE_NAME = "status_cache";

    private static final String KEY_CACHE_HOST = "statusCacheHost";
    private static final String KEY_CACHE_PORT = "statusCachePort";

    public XMPPServerConnectionEventHandler(ClientBootstrap bootstrap, EventDispatcher dispatcher) {
        super(bootstrap, dispatcher);
    }

    @Override
    public void handle(EventContext ctx) throws IOException {
        EventType event = ctx.getEvent();
        Channel channel = ctx.getChannel();

        long timeoutValue = System.currentTimeMillis() + ConfigConst.HEARTBEAT_TIMEOUT * 1000;
        switch (event) {
        case CLIENT_SERVER_CONNECTED:
            serverChannel = channel;
            sendServerInfo();
            break;
        case CLIENT_SERVER_INFO_ACCEPTED:
            Message data = (Message) ctx.getData();
            updateServerInfo(data);
            startHeartBeat();
            timeoutTime = timeoutValue;
            break;
        default:
            super.handle(ctx);
        }
    }

    private void updateServerInfo(Message data) {
        String domain = new String(data.getData());
        XMPPServer.getInstance().getServerInfo().setXMPPDomain(domain);
    }

    private void sendServerInfo() {
        ConnectionManager connectionManager = XMPPServer.getInstance().getConnectionManager();
        PluginManager pluginManager = XMPPServer.getInstance().getPluginManager();
        Plugin statusPlugin = pluginManager.getPlugin(STATUS_PLUGIN_NAME);
        while (statusPlugin == null) {
            // Make sure status plugin loaded.
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Do nothing
            }

            statusPlugin = pluginManager.getPlugin(STATUS_PLUGIN_NAME);
        }

        int clientPort = connectionManager.getClientListenerPort();
        int clientSSLPort = connectionManager.getClientSSLListenerPort();
        int cmPort = connectionManager.getConnectionManagerListenerPort();

        ServerInfo.Builder serverInfoBuilder = ServerInfo.newBuilder();
        serverInfoBuilder.setIp("");
        serverInfoBuilder.setClientPort(clientPort);
        serverInfoBuilder.setClientSSLPort(clientSSLPort);
        serverInfoBuilder.setCMPort(cmPort);
        serverInfoBuilder.setHash(0);

        // Status plugin loaded, so the cache created.
        Cache<String, String> statusCache = CacheFactory.createCache(CACHE_NAME);
        serverInfoBuilder.setCacheHost(statusCache.get(KEY_CACHE_HOST));
        serverInfoBuilder.setCachePort(Integer.parseInt(statusCache.get(KEY_CACHE_PORT)));

        byte[] data = serverInfoBuilder.build().toByteArray();

        Message serverInfoMessage = new Message(MessageFlag.FLAG_SERVER_INFO, 0, data.length, data);
        serverChannel.write(serverInfoMessage);
    }
}
