package me.isaac.audit.protocol.util;

import io.netty.buffer.ByteBuf;
import me.isaac.audit.protocol.pack.HandshakePayload;
import me.isaac.audit.protocol.pack.MySQLPacket;
import me.isaac.audit.protocol.converter.HandshakeConverter;
import me.isaac.audit.protocol.converter.IConverter;

public class PacketConvertUtil {
    public static MySQLPacket<HandshakePayload> convertToHandshake(ByteBuf byteBuf) {
        MySQLPacket<HandshakePayload> packet = new MySQLPacket<>(byteBuf);
        IConverter<HandshakePayload> converter = new HandshakeConverter();
        HandshakePayload payload = converter.get(packet.getPayloadBytes());
        packet.setPayload(payload);

        return packet;
    }
}
