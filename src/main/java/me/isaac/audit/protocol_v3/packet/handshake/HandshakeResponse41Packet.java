package me.isaac.audit.protocol_v3.packet.handshake;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.isaac.audit.protocol_v3.base.MySQLPacket;
import me.isaac.audit.protocol_v3.base.MySQLPayload;
import me.isaac.audit.protocol_v3.constant.MySQLAuthenticationMethod;
import me.isaac.audit.protocol_v3.constant.MySQLCapabilityFlag;

/**
 * Handshake response above MySQL 4.1 packet protocol.
 *
 * @see <a href="https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::HandshakeResponse41">HandshakeResponse41</a>
 */
@AllArgsConstructor
@Getter
@Setter
public final class HandshakeResponse41Packet implements MySQLPacket {
    private final int sequenceId;

    private final int maxPacketSize;

    private final int characterSet;

    private final String username;

    private byte[] authResponse;

    private int capabilityFlags;

    private String database;

    private String authPluginName;

    public HandshakeResponse41Packet(final MySQLPayload payload) {
        sequenceId = payload.readInt1();
        capabilityFlags = payload.readInt4();
        maxPacketSize = payload.readInt4();
        characterSet = payload.readInt1();
        payload.skipReserved(23);
        username = payload.readStringNul();
        authResponse = readAuthResponse(payload);
        database = readDatabase(payload);
        authPluginName = readAuthPluginName(payload);
    }

    private byte[] readAuthResponse(final MySQLPayload payload) {
        if (0 != (capabilityFlags & MySQLCapabilityFlag.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA.getValue())) {
            return payload.readStringLenencByBytes();
        }
        if (0 != (capabilityFlags & MySQLCapabilityFlag.CLIENT_SECURE_CONNECTION.getValue())) {
            int length = payload.readInt1();
            return payload.readStringFixByBytes(length);
        }
        return payload.readStringNulByBytes();
    }

    private String readDatabase(final MySQLPayload payload) {
        return 0 != (capabilityFlags & MySQLCapabilityFlag.CLIENT_CONNECT_WITH_DB.getValue()) ? payload.readStringNul() : null;
    }

    private String readAuthPluginName(final MySQLPayload payload) {
        return 0 != (capabilityFlags & MySQLCapabilityFlag.CLIENT_PLUGIN_AUTH.getValue()) ? payload.readStringNul() : null;
    }

    /**
     * Set database.
     *
     * @param database database
     */
    public void setDatabase(final String database) {
        this.database = database;
        capabilityFlags |= MySQLCapabilityFlag.CLIENT_CONNECT_WITH_DB.getValue();
    }

    /**
     * Set authentication plugin name.
     *
     * @param mysqlAuthenticationMethod MySQL authentication method
     */
    public void setAuthPluginName(final MySQLAuthenticationMethod mysqlAuthenticationMethod) {
        authPluginName = mysqlAuthenticationMethod.getMethodName();
        capabilityFlags |= MySQLCapabilityFlag.CLIENT_PLUGIN_AUTH.getValue();
    }

    @Override
    public void write(final MySQLPayload payload) {
        payload.writeInt4(capabilityFlags);
        payload.writeInt4(maxPacketSize);
        payload.writeInt1(characterSet);
        payload.writeReserved(23);
        payload.writeStringNul(username);
        writeAuthResponse(payload);
        writeDatabase(payload);
        writeAuthPluginName(payload);
    }

    private void writeAuthResponse(final MySQLPayload payload) {
        if (0 != (capabilityFlags & MySQLCapabilityFlag.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA.getValue())) {
            payload.writeStringLenenc(new String(authResponse));
        } else if (0 != (capabilityFlags & MySQLCapabilityFlag.CLIENT_SECURE_CONNECTION.getValue())) {
            payload.writeInt1(authResponse.length);
            payload.writeBytes(authResponse);
        } else {
            payload.writeStringNul(new String(authResponse));
        }
    }

    private void writeDatabase(final MySQLPayload payload) {
        if (0 != (capabilityFlags & MySQLCapabilityFlag.CLIENT_CONNECT_WITH_DB.getValue())) {
            payload.writeStringNul(database);
        }
    }

    private void writeAuthPluginName(final MySQLPayload payload) {
        if (0 != (capabilityFlags & MySQLCapabilityFlag.CLIENT_PLUGIN_AUTH.getValue())) {
            payload.writeStringNul(authPluginName);
        }
    }
}
