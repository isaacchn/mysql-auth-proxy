package me.isaac.audit.protocol_v3.packet.handshake;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Authentication plugin data for MySQL.
 *
 * <p>
 * The auth-plugin-data is the concatenation of strings auth-plugin-data-part-1 and auth-plugin-data-part-2.
 * The auth-plugin-data-part-1's length is 8; The auth-plugin-data-part-2's length is 12.
 * </p>
 */
@AllArgsConstructor
@Getter
public final class MySQLAuthPluginData {
    private final byte[] authPluginDataPart1;

    private final byte[] authPluginDataPart2;

    public MySQLAuthPluginData() {
        this(MySQLRandomGenerator.getINSTANCE().generateRandomBytes(8), MySQLRandomGenerator.getINSTANCE().generateRandomBytes(12));
    }

    /**
     * Get authentication plugin data.
     *
     * @return authentication plugin data
     */
    public byte[] getAuthenticationPluginData() {
        //todo return Bytes.concat(authPluginDataPart1, authPluginDataPart2);
        return ArrayUtil.addAll(authPluginDataPart1, authPluginDataPart2);
    }
}
