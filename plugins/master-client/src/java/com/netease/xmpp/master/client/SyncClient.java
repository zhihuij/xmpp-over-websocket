package com.netease.xmpp.master.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.netease.xmpp.master.common.ConfigConst;
import com.netease.xmpp.master.common.MessageDecoder;
import com.netease.xmpp.master.event.EventDispatcher;
import com.netease.xmpp.master.event.EventType;
import com.netease.xmpp.master.event.client.ServerConnectionEventHandler;
import com.netease.xmpp.util.ResourceUtils;

public class SyncClient {
    private static final String CONFIG_FILE = "master.properties";

    public static final int CLIENT_TYPE_XMPP_SERVER = 1;
    public static final int CLIENT_TYPE_PROXY = 2;
    public static final int CLIENT_TYPE_ROBOT = 3;

    private int clientType = CLIENT_TYPE_XMPP_SERVER;

    private String configPath = null;

    public SyncClient(int clientType) {
        this.clientType = clientType;
    }

    public void setConfigPath(String path) {
        this.configPath = path;
    }

    private boolean loadConfig(ClientConfigCache config, int clientType) {
        try {
            Properties prop = new Properties();
            InputStream input = null;
            String configFilePath = CONFIG_FILE;
            if (configPath != null) {
                configFilePath = configPath + File.separator + CONFIG_FILE;
                input = new FileInputStream(new File(configFilePath));
            } else {
                input = ResourceUtils.getResourceAsStream(configFilePath);
            }

            prop.load(input);

            config.setHashAlgorithmClassName(prop.getProperty(ConfigConst.KEY_HASH_CLASS_NAME,
                    ConfigConst.DEFAULT_HASH_CLASS_NAME));

            config
                    .setMasterServerHost(prop.getProperty(ConfigConst.KEY_MASTER_SERVER,
                            "localhost"));

            switch (clientType) {
            case CLIENT_TYPE_XMPP_SERVER:
                String xmppServerPort = prop.getProperty(ConfigConst.KEY_XMPP_SERVER_PORT, String
                        .valueOf(ConfigConst.DEFAULT_XMPP_SERVER_PORT));
                config.setMasterServerPort(Integer.valueOf(xmppServerPort));
                break;
            case CLIENT_TYPE_PROXY:
                String proxyPort = prop.getProperty(ConfigConst.KEY_PROXY_PORT, String
                        .valueOf(ConfigConst.DEFAULT_PROXY_PORT));
                config.setMasterServerPort(Integer.valueOf(proxyPort));
                break;
            case CLIENT_TYPE_ROBOT:
                String robotPort = prop.getProperty(ConfigConst.KEY_ROBOT_PORT, String
                        .valueOf(ConfigConst.DEFAULT_ROBOT_PORT));
                config.setMasterServerPort(Integer.valueOf(robotPort));
                break;
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    public void start() {
        ClientConfigCache clientConfig = ClientConfigCache.getInstance();
        if (!loadConfig(clientConfig, clientType)) {
            return;
        }

        ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final EventDispatcher eventDispatcher = EventDispatcher.getInstance();

        ClientBootstrap bootstrap = new ClientBootstrap(factory);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(new MessageDecoder(), new ServerChannelHandler(
                        eventDispatcher));
            }
        });
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        bootstrap.setOption("connectTimeoutMillis", 5000);
        bootstrap.setOption("remoteAddress", new InetSocketAddress(clientConfig
                .getMasterServerHost(), clientConfig.getMasterServerPort()));

        ServerConnectionEventHandler serverConnectionEventHandler = new ServerConnectionEventHandler(
                bootstrap, eventDispatcher);

        eventDispatcher.registerEvent(serverConnectionEventHandler, //
                EventType.CLIENT_SERVER_CONNECTED, //
                EventType.CLIENT_SERVER_INFO_ACCEPTED, //
                EventType.CLIENT_SERVER_DISCONNECTED, //
                EventType.CLIENT_SERVER_HEARTBEAT, //
                EventType.CLIENT_SERVER_HEARTBEAT_TIMOUT);

        ChannelFuture f = bootstrap.connect().awaitUninterruptibly();
        if (!f.isSuccess()) {
            eventDispatcher.dispatchEvent(null, null, EventType.CLIENT_SERVER_DISCONNECTED);
        }
    }

    public static void main(String[] args) throws Exception {
        int clientType = CLIENT_TYPE_PROXY;
        if (args.length > 0) {
            clientType = Integer.valueOf(args[0]);
        }

        new SyncClient(clientType).start();
    }
}
