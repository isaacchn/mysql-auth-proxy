package me.isaac.audit.protocol_v2.basic_type;

import io.netty.buffer.ByteBuf;

/**
 * 定长整型
 */
public class FixedLengthInt extends MysqlBasicData {
    private int length;

    public FixedLengthInt(int length) {
        this.length = length;
    }

    @Override
    public void readFrom(ByteBuf buf) {

    }
}
