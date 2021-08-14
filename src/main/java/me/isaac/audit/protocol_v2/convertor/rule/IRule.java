package me.isaac.audit.protocol_v2.convertor.rule;

import io.netty.buffer.ByteBuf;

/**
 * 从字节数组缓冲区读取数据的规则
 */
public interface IRule {
    byte[] readFromByte(ByteBuf byteBuf);
}
