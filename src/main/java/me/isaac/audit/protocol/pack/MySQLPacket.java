package me.isaac.audit.protocol.pack;


import cn.hutool.core.util.ArrayUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.Data;
import me.isaac.audit.protocol.util.FixedLengthUtil;

@Data
public class MySQLPacket<T extends Payload> {
    private int payloadLength; //消息长度
    private int sequenceId; //序号
    private byte[] payloadBytes;
    private T payload; //消息体

    public MySQLPacket(ByteBuf byteBuf) {
        //byte[] bytes = new byte[3];
        //for (int i = 0; i < 3; i++) {
        //    bytes[i] = byteBuf.getByte(i);
        //}
        //payloadLength = FixedLengthUtil.readUB3(bytes);
        //sequenceId = FixedLengthUtil.readUB1(byteBuf.getByte(3));
        byte[] src = ByteBufUtil.getBytes(byteBuf);
        payloadLength = FixedLengthUtil.readUB3(ArrayUtil.sub(src, 0, 3));
        sequenceId = FixedLengthUtil.readUB1(src[3]);
        payloadBytes = ArrayUtil.sub(src, 4, src.length);
    }
}
