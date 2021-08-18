package me.isaac.audit.protocol_v2.basic_type;

import io.netty.buffer.ByteBuf;
import me.isaac.audit.util.NumberUtil;

/**
 * 定长整型
 */
public class FixedLengthInt extends MysqlBasicData implements IntType{
    public FixedLengthInt(int length) {
        this.bytes = new byte[length];
    }

    @Override
    public void readFrom(ByteBuf buf) {
        buf.readBytes(this.bytes);
    }

    @Override
    public int intValue() {
        return NumberUtil.bytesToInt(this.bytes);
    }
}
