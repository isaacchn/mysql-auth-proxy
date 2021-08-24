package me.isaac.audit.protocol_v3.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import me.isaac.audit.protocol_v3.base.MySQLPacket;

import java.util.List;

@Slf4j
public class PacketCodec extends ByteToMessageCodec<MySQLPacket> {
    private final DatabasePacketCodecEngine databasePacketCodecEngine;

    public PacketCodec(DatabasePacketCodecEngine databasePacketCodecEngine) {
        this.databasePacketCodecEngine = databasePacketCodecEngine;
    }

    @Override
    protected void decode(final ChannelHandlerContext context, final ByteBuf in, final List<Object> out) {
        int readableBytes = in.readableBytes();
        if (!databasePacketCodecEngine.isValidHeader(readableBytes)) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Read from client {} : \n {}", context.channel().id().asShortText(), ByteBufUtil.prettyHexDump(in));
        }
        databasePacketCodecEngine.decode(context, in, out);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void encode(final ChannelHandlerContext context, final MySQLPacket message, final ByteBuf out) {
        databasePacketCodecEngine.encode(context, message, out);
        if (log.isDebugEnabled()) {
            log.debug("Write to client {} : \n {}", context.channel().id().asShortText(), ByteBufUtil.prettyHexDump(out));
        }
    }
}
