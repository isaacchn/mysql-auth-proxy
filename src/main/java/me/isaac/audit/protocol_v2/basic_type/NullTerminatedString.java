package me.isaac.audit.protocol_v2.basic_type;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

public class NullTerminatedString extends MysqlBasicData {
    @Override
    public void readFrom(ByteBuf buf) {
        List<Byte> byteList = new LinkedList<>();
        int size = 0;
        while (true) {
            byte b = buf.readByte();
            byteList.add(b);
            size += 1;
            if (b == 0) {
                break;
            }
        }
        this.bytes = new byte[size];
        int pos = 0;
        for (Byte aByte : byteList) {
            this.bytes[pos] = aByte;
            pos++;
        }
    }
}
