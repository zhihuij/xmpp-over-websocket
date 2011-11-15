package com.netease.xmpp.master.common;

/**
 * Const for config, including keys and default values.
 * 
 * @author jiaozhihui@corp.netease.com
 */
public class ConfigConst {
    /**
     * Keys for config.
     */
    public static final String KEY_MASTER_SERVER = "master.server.host";

    public static final String KEY_SERVER_REP_NUM = "master.server.numReps";

    public static final String KEY_SERVER_MONITOR_INTERVAL = "master.server.monitor.interval";

    public static final String KEY_HASH_CLASS_NAME = "master.hash.className";

    public static final String KEY_HASH_FILE_PATH = "master.hash.file";

    public static final String KEY_HASH_MONITOR_INTERVAL = "master.hash.monitor.interval";

    public static final String KEY_XMPP_SERVER_PORT = "master.xmppServer.port";

    public static final String KEY_PROXY_PORT = "master.proxy.port";

    public static final String KEY_ROBOT_PORT = "master.robot.port";

    /**
     * Default values for config.
     */
    public static final int DEFAULT_SERVER_REP_NUM = 160;

    public static final int DEFAULT_SERVER_MONITOR_INTERVAL = 5;

    public static final String DEFAULT_HASH_CLASS_NAME = "com.netease.xmpp.hash.server.HashAlgorithmImpl";

    public static final String DEFAULT_HASH_FILE_PATH = "HashAlgorithmImpl.class";

    public static final int DEFAULT_HASH_MONITOR_INTERVAL = 60;

    public static final int DEFAULT_XMPP_SERVER_PORT = 8080;

    public static final int DEFAULT_PROXY_PORT = 8081;

    public static final int DEFAULT_ROBOT_PORT = 8082;

    /**
     * Constants.
     */
    public static final int HEARTBEAT_INTERVAL = 30;

    public static final int HEARTBEAT_TIMEOUT = 3 * 30;

    public static final String CLIENT_PROXY = "PROXY";

    public static final String CLIENT_ROBOT = "ROBOT";

    public static final String CLIENT_XMPP_SERVER = "SERVER";
}
