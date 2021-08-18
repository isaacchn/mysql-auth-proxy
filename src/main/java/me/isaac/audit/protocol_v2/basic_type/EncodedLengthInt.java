package me.isaac.audit.protocol_v2.basic_type;

import io.netty.buffer.ByteBuf;
import me.isaac.audit.protocol.util.CustomByteUtil;
import me.isaac.audit.util.NumberUtil;

public class EncodedLengthInt extends MysqlBasicData implements IntType{
    /**
     * If the first byte is less than 0xfb ( < 251 ) then next one byte is valuable (it is stored as a 1-byte integer)
     * If the first byte is equal to 0xfc ( == 252 ) then it is stored as a 2-byte integer
     * If the first byte is equal to 0xfd ( == 253 ) then it is stored as a 3-byte integer
     * If the first byte is equal to 0xfe ( == 254 ) then it is stored as a 8-byte integer
     */
    @Override
    public void readFrom(ByteBuf buf) {
        int first = CustomByteUtil.byteToInt(buf.readByte());
        if (first < 0xfb) {
            this.bytes = new byte[1];
            bytes[0] = CustomByteUtil.intToByte(first);
        } else if (first == 0xfc) {
            this.bytes = new byte[3];
            bytes[0] = CustomByteUtil.intToByte(first);
            buf.readBytes(this.bytes, 1, 2);
        } else if (first == 0xfd) {
            this.bytes = new byte[4];
            bytes[0] = CustomByteUtil.intToByte(first);
            buf.readBytes(this.bytes, 1, 3);
        } else if (first == 0xfe) {
            this.bytes = new byte[9];
            bytes[0] = CustomByteUtil.intToByte(first);
            buf.readBytes(this.bytes, 1, 8);
        } else {
            //todo
            this.bytes = new byte[1];
        }
    }

    @Override
    public int intValue() {
        return NumberUtil.bytesToInt(this.bytes);
    }
}
