package me.isaac.audit.protocol_v3.packet.generic;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.isaac.audit.protocol_v3.base.MySQLPacket;
import me.isaac.audit.protocol_v3.base.MySQLPayload;
import me.isaac.audit.protocol_v3.constant.MySQLStatusFlag;

@RequiredArgsConstructor
@Getter
public final class MySQLOKPacket implements MySQLPacket {
    /**
     * Header of OK packet.
     */
    public static final int HEADER = 0x00;

    private static final int DEFAULT_STATUS_FLAG = MySQLStatusFlag.SERVER_STATUS_AUTOCOMMIT.getValue();

    private final int sequenceId;

    private final long affectedRows;

    private final long lastInsertId;

    private final int statusFlag;

    private final int warnings;

    private final String info;

    public MySQLOKPacket(final int sequenceId) {
        this(sequenceId, 0L, 0L, DEFAULT_STATUS_FLAG, 0, "");
    }

    public MySQLOKPacket(final int sequenceId, final long affectedRows, final long lastInsertId) {
        this(sequenceId, affectedRows, lastInsertId, DEFAULT_STATUS_FLAG, 0, "");
    }

    public MySQLOKPacket(final MySQLPayload payload) {
        sequenceId = payload.readInt1();
        Assert.isTrue(HEADER == payload.readInt1(), "Header of MySQL OK packet must be `0x00`.");
        affectedRows = payload.readIntLenenc();
        lastInsertId = payload.readIntLenenc();
        statusFlag = payload.readInt2();
        warnings = payload.readInt2();
        info = payload.readStringEOF();
    }

    @Override
    public void write(final MySQLPayload payload) {
        payload.writeInt1(HEADER);
        payload.writeIntLenenc(affectedRows);
        payload.writeIntLenenc(lastInsertId);
        payload.writeInt2(statusFlag);
        payload.writeInt2(warnings);
        payload.writeStringEOF(info);
    }
}
