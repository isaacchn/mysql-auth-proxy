package me.isaac.audit.protocol_v3.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import me.isaac.audit.protocol_v3.base.DbPayload;
import me.isaac.audit.protocol_v3.base.MySQLPacket;
import me.isaac.audit.protocol_v3.base.MySQLPayload;
import me.isaac.audit.protocol_v3.constant.CommonErrorCode;
import me.isaac.audit.protocol_v3.packet.generic.MySQLErrPacket;

import java.util.List;

public final class MySQLPacketCodecEngine implements DatabasePacketCodecEngine<MySQLPacket> {
    private static final int PAYLOAD_LENGTH = 3;

    private static final int SEQUENCE_LENGTH = 1;

    @Override
    public boolean isValidHeader(int readableBytes) {
        return readableBytes >= PAYLOAD_LENGTH + SEQUENCE_LENGTH;
    }

    @Override
    public void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) {
        int payloadLength = in.markReaderIndex().readMediumLE();
        int remainPayloadLength = SEQUENCE_LENGTH + payloadLength;
        if (in.readableBytes() < remainPayloadLength) {
            in.resetReaderIndex();
            return;
        }
        out.add(in.readRetainedSlice(SEQUENCE_LENGTH + payloadLength));
    }

    @Override
    public void encode(ChannelHandlerContext context, MySQLPacket message, ByteBuf out) {
        MySQLPayload payload = new MySQLPayload(context.alloc().buffer());
        try {
            message.write(payload);
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            payload.getByteBuf().resetWriterIndex();
            new MySQLErrPacket(1, CommonErrorCode.UNKNOWN_EXCEPTION, ex.getMessage()).write(payload);
        } finally {
            out.writeMediumLE(payload.getByteBuf().readableBytes());
            out.writeByte(message.getSequenceId());
            out.writeBytes(payload.getByteBuf());
            payload.close();
        }
    }

    @Override
    public DbPayload createPacketPayload(ByteBuf message) {
        return new MySQLPayload(message);
    }
}
