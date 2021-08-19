package me.isaac.audit.protocol_v3.base;

import io.netty.buffer.ByteBuf;

public interface DbPayload extends AutoCloseable {
    ByteBuf getByteBuf();
}
