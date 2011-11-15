package com.netease.xmpp.master.event.client;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipelineException;
import org.jivesoftware.openfire.ConnectionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.util.cache.Cache;
import org.jivesoftware.util.cache.CacheFactory;

import com.netease.xmpp.master.common.ConfigConst;
import com.netease.xmpp.master.common.HeartBeatWorker;
import com.netease.xmpp.master.common.Message;
import com.netease.xmpp.master.common.MessageFlag;
import com.netease.xmpp.master.common.ServerListProtos.Server.ServerInfo;
import com.netease.xmpp.master.event.EventContext;
import com.netease.xmpp.master.event.EventDispatcher;
import com.netease.xmpp.master.event.EventHandler;
import com.netease.xmpp.master.event.EventType;
import com.netease.xmpp.master.event.UnrecognizedEvent;

public class ServerConnectionEventHandler implements EventHandler {
    private static Logger logger = Logger.getLogger(ServerConnectionEventHandler.class);

    private static final String STATUS_PLUGIN_NAME = "status";

    private static final String CACHE_NAME = "status_cache";

    private static final String KEY_CACHE_HOST = "statusCacheHost";
    private static final String KEY_CACHE_PORT = "statusCachePort";

    private ClientBootstrap bootstrap = null;

    private HeartBeatWorker worker = null;

    private AtomicBoolean isDone = new AtomicBoolean(false);

    private AtomicLong timeoutTime = new AtomicLong(-1);

    private Channel serverChannel = null;

    private Thread timeoutChecker = null;

    private EventDispatcher eventDispatcher = null;

    public ServerConnectionEventHandler(ClientBootstrap bootstrap, EventDispatcher dispatcher) {
        this.bootstrap = bootstrap;
        this.eventDispatcher = dispatcher;

        timeoutChecker = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    synchronized (timeoutTime) {
                        if (timeoutTime.get() > 0) {
                            if (timeoutTime.get() <= System.currentTimeMillis()) {
                                logger.debug("SERVER_HEARTBEAT_TIMOUT");
                                eventDispatcher.dispatchEvent(null, null,
                                        EventType.CLIENT_SERVER_HEARTBEAT_TIMOUT);
                            }
                        }
                    }

                    try {
                        Thread.sleep(ConfigConst.HEARTBEAT_INTERVAL * 1000);
                    } catch (InterruptedException e) {
                        // Do nothing
                    }
                }
            }
        });

        timeoutChecker.start();
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
            startHeartBeat(serverChannel);
            synchronizedSet(timeoutTime, timeoutValue);
            break;
        case CLIENT_SERVER_HEARTBEAT_TIMOUT:
            synchronizedSet(timeoutTime, -1);
            serverChannel.close().awaitUninterruptibly();
            break;

        case CLIENT_SERVER_DISCONNECTED:
            reconnect();
            break;

        case CLIENT_SERVER_HEARTBEAT:
            synchronizedSet(timeoutTime, timeoutValue);
            break;
        default:
            throw new UnrecognizedEvent(event.toString());
        }
    }

    private void updateServerInfo(Message data) {
        String domain = new String(data.getData());
        XMPPServer.getInstance().getServerInfo().setXMPPDomain(domain);
    }

    private void synchronizedSet(AtomicLong timeoutTime, long value) {
        synchronized (timeoutTime) {
            timeoutTime.set(value);
        }
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

    private void startHeartBeat(Channel channel) {
        worker = new HeartBeatWorker(channel);
        worker.start();
    }

    private synchronized void reconnect() {
        if (worker != null) {
            worker.stop();
        }

        while (true) {
            logger.info("START RECONNECTING......");

            isDone.set(false);
            try {
                ChannelFuture f = bootstrap.connect();
                f.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            isDone.set(true);
                        }
                    }
                });

                Thread.sleep(10 * 1000);

                if (isDone.get()) {
                    logger.info("SERVER CONNECTED");
                    break;
                }
            } catch (ChannelPipelineException e) {
                // Do nothing
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
    }
}
