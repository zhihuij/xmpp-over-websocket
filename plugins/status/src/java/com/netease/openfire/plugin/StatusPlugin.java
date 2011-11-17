package com.netease.openfire.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.event.SessionEventDispatcher;
import org.jivesoftware.openfire.event.SessionEventListener;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.PresenceEventDispatcher;
import org.jivesoftware.openfire.user.PresenceEventListener;
import org.jivesoftware.util.cache.Cache;
import org.jivesoftware.util.cache.CacheFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisException;

public class StatusPlugin implements Plugin, PresenceEventListener, SessionEventListener {
    private static Logger logger = Logger.getLogger(StatusPlugin.class);

    private static final String ONLINE_SIGN = "ON";

    private static final String CACHE_NAME = "status_cache";

    private static final String KEY_CACHE_HOST = "statusCacheHost";
    private static final String KEY_CACHE_PORT = "statusCachePort";

    private JedisPool pool = null;

    private String cacheHost = null;
    private int cachePort = Protocol.DEFAULT_PORT;

    @Override
    public void initializePlugin(PluginManager manager, File pluginDirectory) {
        Properties pro = new Properties();
        try {
            pro.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(
                    "status.properties"));
        } catch (IOException e) {
            System.out.println("Load status.properties failed");
            return;
        }

        String host = pro.getProperty("cache_host");
        String port = pro.getProperty("cache_port");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxActive(30);
        poolConfig.setMinIdle(30);
        poolConfig.setMinEvictableIdleTimeMillis(500);
        // Validate Jedis connection before returned
        poolConfig.setTestOnBorrow(true);

        if (port != null) {
            try {
                cachePort = Integer.valueOf(port);
            } catch (NumberFormatException e) {
                // Do nothing
            }
        }

        cacheHost = host;
        pool = new JedisPool(poolConfig, cacheHost, cachePort);

        Cache<String, String> statusCache = CacheFactory.createCache(CACHE_NAME);
        statusCache.put(KEY_CACHE_HOST, cacheHost);
        statusCache.put(KEY_CACHE_PORT, String.valueOf(cachePort));

        PresenceEventDispatcher.addListener(this);
        SessionEventDispatcher.addListener(this);

        logger.debug("Status server initiated sucessfully on: " + host);
    }

    @Override
    public void destroyPlugin() {
        if (pool != null) {
            Jedis jedis = pool.getResource();
            try {
                jedis.flushAll();
                logger.debug(">>>Flush all status.");
            } catch (JedisException ex) {
                logger.error("Jedis error: " + ex.getMessage());
            } finally {
                pool.returnResource(jedis);
            }

            pool.destroy();
        }

        PresenceEventDispatcher.removeListener(this);
        SessionEventDispatcher.removeListener(this);
    }

    private void addOnlineUser(String user) {
        if (pool == null) {
            return;
        }

        Jedis jedis = pool.getResource();

        try {
            jedis.set(user, ONLINE_SIGN);

            logger.debug(">>>Add online user: " + user);
        } catch (JedisException ex) {
            logger.error("Jedis error: " + ex.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }

    private void delOnlineUser(String user) {
        if (pool == null) {
            return;
        }

        Jedis jedis = pool.getResource();

        try {
            String value = jedis.get(user);
            if (value != null) {
                jedis.del(user);

                logger.debug(">>>Delete online user: " + user);
            }
        } catch (JedisException ex) {
            logger.error("Jedis error: " + ex.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Override
    public void availableSession(ClientSession session, Presence presence) {
        logger.debug(">>>Available session: " + session.getAddress().getNode());
        addOnlineUser(session.getAddress().getNode());
    }

    @Override
    public void unavailableSession(ClientSession session, Presence presence) {
        logger.debug(">>>Unavailable session: " + session.getAddress().getNode());
        delOnlineUser(session.getAddress().getNode());
    }

    @Override
    public void presenceChanged(ClientSession session, Presence presence) {
        // Do nothing
    }

    @Override
    public void subscribedToPresence(JID subscriberJID, JID authorizerJID) {
        // Do nothing
    }

    @Override
    public void unsubscribedToPresence(JID unsubscriberJID, JID recipientJID) {
        // Do nothing
    }

    @Override
    public void sessionCreated(Session session) {
        // Do nothing
    }

    @Override
    public void sessionDestroyed(Session session) {
        logger.debug(">>>Session destroyed: " + session.getAddress().getNode());
        delOnlineUser(session.getAddress().getNode());
    }

    @Override
    public void anonymousSessionCreated(Session session) {
        // Do nothing
    }

    @Override
    public void anonymousSessionDestroyed(Session session) {
        // Do nothing
    }

    @Override
    public void resourceBound(Session session) {
        // Do nothing
    }
}
