package me.isaac.audit.protocol_v3.packet.generic;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.isaac.audit.protocol_v3.base.MySQLPacket;
import me.isaac.audit.protocol_v3.base.MySQLPayload;
import me.isaac.audit.protocol_v3.constant.SQLErrorCode;

/**
 * ERR packet protocol for MySQL.
 *
 * @see <a href="https://dev.mysql.com/doc/internals/en/packet-ERR_Packet.html">ERR Packet</a>
 */
@RequiredArgsConstructor
@Getter
public final class MySQLErrPacket implements MySQLPacket {
    /**
     * Header of ERR packet.
     */
    public static final int HEADER = 0xff;

    private static final String SQL_STATE_MARKER = "#";

    private final int sequenceId;

    private final int errorCode;

    private final String sqlState;

    private final String errorMessage;

    public MySQLErrPacket(final int sequenceId, final SQLErrorCode sqlErrorCode, final Object... errorMessageArguments) {
        this(sequenceId, sqlErrorCode.getErrorCode(), sqlErrorCode.getSqlState(), String.format(sqlErrorCode.getErrorMessage(), errorMessageArguments));
    }

    public MySQLErrPacket(final MySQLPayload payload) {
        sequenceId = payload.readInt1();
        //Preconditions.checkArgument(HEADER == payload.readInt1(), "Header of MySQL ERR packet must be `0xff`.");
        Assert.isTrue(HEADER == payload.readInt1(), "Header of MySQL ERR packet must be `0xff`.");
        errorCode = payload.readInt2();
        payload.readStringFix(1);
        sqlState = payload.readStringFix(5);
        errorMessage = payload.readStringEOF();
    }

    @Override
    public void write(final MySQLPayload payload) {
        payload.writeInt1(HEADER);
        payload.writeInt2(errorCode);
        payload.writeStringFix(SQL_STATE_MARKER);
        payload.writeStringFix(sqlState);
        payload.writeStringEOF(errorMessage);
    }
}
