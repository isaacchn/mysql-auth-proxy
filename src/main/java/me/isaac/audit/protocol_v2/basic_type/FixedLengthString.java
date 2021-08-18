package me.isaac.audit.protocol_v2.basic_type;

import io.netty.buffer.ByteBuf;

public class FixedLengthString extends MysqlBasicData {
    public FixedLengthString(int length) {
        this.bytes = new byte[length];
    }

    @Override
    public void readFrom(ByteBuf buf) {
        buf.readBytes(this.bytes);
    }
}
