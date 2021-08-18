package me.isaac.audit.protocol_v2.basic_type;

import io.netty.buffer.ByteBuf;

public class EofString extends MysqlBasicData {
    @Override
    public void readFrom(ByteBuf buf) {
        int readable = buf.readableBytes();
        this.bytes = new byte[readable];
        buf.readBytes(bytes);
    }
}
