package me.isaac.audit.protocol_v3.constant;

public final class MySQLServerInfo {
    /**
     * Protocol version is always 0x0A.
     */
    public static final int PROTOCOL_VERSION = 0x0A;

    /**
     * Charset code 0x21 is utf8_general_ci.
     */
    public static final int CHARSET = 0x21;

    private static final String DEFAULT_MYSQL_VERSION = "5.7.22";

    private static final String PROXY_VERSION = "5.0.0-beta";

    private static final String SERVER_VERSION_PATTERN = "%s-ShardingSphere-Proxy %s";

    private static volatile String serverVersion;

    /**
     * Set server version.
     *
     * @param serverVersion server version
     */
    public static synchronized void setServerVersion(final String serverVersion) {
        MySQLServerInfo.serverVersion = null == serverVersion ? null : String.format(SERVER_VERSION_PATTERN, serverVersion, PROXY_VERSION);
    }

    /**
     * Get current server version.
     *
     * @return server version
     */
    public static String getServerVersion() {
        return null == serverVersion ? String.format(SERVER_VERSION_PATTERN, DEFAULT_MYSQL_VERSION, PROXY_VERSION) : serverVersion;
    }
}
