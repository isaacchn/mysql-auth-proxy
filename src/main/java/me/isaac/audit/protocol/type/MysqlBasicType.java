package me.isaac.audit.protocol.type;

import io.netty.buffer.ByteBuf;

/**
 * MySQL内置数据类型
 */
public abstract class MysqlBasicType {
    public MysqlBasicType(ByteBuf buf) {
        read(buf);
    }

    //从buffer中读取数据，同时移动游标的位置
    public abstract void read(ByteBuf buf);

    public abstract byte[] bytesValue();

    public abstract String formatValue();
}
