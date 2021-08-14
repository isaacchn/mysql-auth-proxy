package me.isaac.audit.protocol_v2.basic_type;

import io.netty.buffer.ByteBuf;

/**
 * MySQL内置数据类型
 */
public abstract class MysqlBasicData {
    private byte[] bytes;

//    public MysqlBasicData(byte[] bytes) {
//        this.bytes = bytes;
//    }

    //从buffer中读取数据，同时移动游标的位置
    public abstract void readFrom(ByteBuf buf);
//
//    public byte[] bytesValue() {
//        return this.bytes;
//    }
//
    //public abstract String formatValue();
}
