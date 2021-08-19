package me.isaac.audit.protocol_v3.packet.generic;

import cn.hutool.core.lang.Assert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.isaac.audit.protocol_v3.base.MySQLPacket;
import me.isaac.audit.protocol_v3.base.MySQLPayload;
import me.isaac.audit.protocol_v3.constant.MySQLStatusFlag;

@AllArgsConstructor
@Getter
public final class MySQLEofPacket implements MySQLPacket {
    /**
     * Header of EOF packet.
     */
    public static final int HEADER = 0xfe;

    private final int sequenceId;

    private final int warnings;

    private final int statusFlags;

    public MySQLEofPacket(final int sequenceId) {
        this(sequenceId, 0, MySQLStatusFlag.SERVER_STATUS_AUTOCOMMIT.getValue());
    }

    public MySQLEofPacket(final MySQLPayload payload) {
        sequenceId = payload.readInt1();
        //todo Preconditions.checkArgument(HEADER == payload.readInt1(), "Header of MySQL EOF packet must be `0xfe`.");
        Assert.isTrue(HEADER == payload.readInt1());
        warnings = payload.readInt2();
        statusFlags = payload.readInt2();
    }

    @Override
    public void write(final MySQLPayload payload) {
        payload.writeInt1(HEADER);
        payload.writeInt2(warnings);
        payload.writeInt2(statusFlags);
    }
}
