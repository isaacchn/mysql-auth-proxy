package me.isaac.audit.protocol_v3.packet.handshake;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import me.isaac.audit.protocol_v3.base.MySQLPacket;
import me.isaac.audit.protocol_v3.base.MySQLPayload;
import me.isaac.audit.protocol_v3.constant.MySQLAuthenticationMethod;
import me.isaac.audit.protocol_v3.constant.MySQLCapabilityFlag;
import me.isaac.audit.protocol_v3.constant.MySQLServerInfo;
import me.isaac.audit.protocol_v3.constant.MySQLStatusFlag;

/**
 * Handshake packet protocol for MySQL.
 *
 * @see <a href="https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::Handshake">Handshake</a>
 */
@Getter
public final class HandshakePacket implements MySQLPacket {
    private final int protocolVersion = MySQLServerInfo.PROTOCOL_VERSION;

    private final String serverVersion;

    private final int connectionId;

    private final int capabilityFlagsLower;

    private final int characterSet;

    private final MySQLStatusFlag statusFlag;

    private final MySQLAuthPluginData authPluginData;

    private int capabilityFlagsUpper;

    private String authPluginName;

    public HandshakePacket(final int connectionId, final MySQLAuthPluginData authPluginData) {
        serverVersion = MySQLServerInfo.getServerVersion();
        this.connectionId = connectionId;
        capabilityFlagsLower = MySQLCapabilityFlag.calculateHandshakeCapabilityFlagsLower();
        characterSet = MySQLServerInfo.CHARSET;
        statusFlag = MySQLStatusFlag.SERVER_STATUS_AUTOCOMMIT;
        capabilityFlagsUpper = MySQLCapabilityFlag.calculateHandshakeCapabilityFlagsUpper();
        this.authPluginData = authPluginData;
        authPluginName = MySQLAuthenticationMethod.SECURE_PASSWORD_AUTHENTICATION.getMethodName();
    }

    public HandshakePacket(final MySQLPayload payload) {
        //todo 检查
        //Preconditions.checkArgument(0 == payload.readInt1(), "Sequence ID of MySQL handshake packet must be `0`.");
        //Preconditions.checkArgument(protocolVersion == payload.readInt1());
        Assert.isTrue(0 == payload.readInt1());
        Assert.isTrue(protocolVersion == payload.readInt1());
        serverVersion = payload.readStringNul();
        connectionId = payload.readInt4();
        final byte[] authPluginDataPart1 = payload.readStringNulByBytes();
        capabilityFlagsLower = payload.readInt2();
        characterSet = payload.readInt1();
        statusFlag = MySQLStatusFlag.valueOf(payload.readInt2());
        capabilityFlagsUpper = payload.readInt2();
        payload.readInt1();
        payload.skipReserved(10);
        authPluginData = new MySQLAuthPluginData(authPluginDataPart1, readAuthPluginDataPart2(payload));
        authPluginName = readAuthPluginName(payload);
    }

    /**
     * There are some different between implement of handshake initialization packet and document.
     * In source code of 5.7 version, authPluginDataPart2 should be at least 12 bytes,
     * and then follow a nul byte.
     * But in document, authPluginDataPart2 is at least 13 bytes, and not nul byte.
     * From test, the 13th byte is nul byte and should be excluded from authPluginDataPart2.
     *
     * @param payload MySQL packet payload
     */
    private byte[] readAuthPluginDataPart2(final MySQLPayload payload) {
        return isClientSecureConnection() ? payload.readStringNulByBytes() : new byte[0];
    }

    private String readAuthPluginName(final MySQLPayload payload) {
        return isClientPluginAuth() ? payload.readStringNul() : null;
    }

    /**
     * Set authentication plugin name.
     *
     * @param mysqlAuthenticationMethod MySQL authentication method
     */
    public void setAuthPluginName(final MySQLAuthenticationMethod mysqlAuthenticationMethod) {
        authPluginName = mysqlAuthenticationMethod.getMethodName();
        capabilityFlagsUpper |= MySQLCapabilityFlag.CLIENT_PLUGIN_AUTH.getValue() >> 16;
    }

    @Override
    public void write(final MySQLPayload payload) {
        payload.writeInt1(protocolVersion);
        payload.writeStringNul(serverVersion);
        payload.writeInt4(connectionId);
        payload.writeStringNul(new String(authPluginData.getAuthPluginDataPart1()));
        payload.writeInt2(capabilityFlagsLower);
        payload.writeInt1(characterSet);
        payload.writeInt2(statusFlag.getValue());
        payload.writeInt2(capabilityFlagsUpper);
        payload.writeInt1(isClientPluginAuth() ? authPluginData.getAuthenticationPluginData().length + 1 : 0);
        payload.writeReserved(10);
        writeAuthPluginDataPart2(payload);
        writeAuthPluginName(payload);
    }

    private void writeAuthPluginDataPart2(final MySQLPayload payload) {
        if (isClientSecureConnection()) {
            payload.writeStringNul(new String(authPluginData.getAuthPluginDataPart2()));
        }
    }

    private void writeAuthPluginName(final MySQLPayload payload) {
        if (isClientPluginAuth()) {
            payload.writeStringNul(authPluginName);
        }
    }

    private boolean isClientSecureConnection() {
        return 0 != (capabilityFlagsLower & MySQLCapabilityFlag.CLIENT_SECURE_CONNECTION.getValue() & 0x00000ffff);
    }

    private boolean isClientPluginAuth() {
        return 0 != (capabilityFlagsUpper & MySQLCapabilityFlag.CLIENT_PLUGIN_AUTH.getValue() >> 16);
    }

    @Override
    public int getSequenceId() {
        return 0;
    }
}
