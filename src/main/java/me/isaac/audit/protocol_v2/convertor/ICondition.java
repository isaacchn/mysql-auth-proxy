package me.isaac.audit.protocol_v2.convertor;

import io.netty.buffer.ByteBuf;
import me.isaac.audit.protocol_v2.basic_type.MysqlBasicData;

public interface ICondition {
    void readByCondition(ByteBuf buf);
}
