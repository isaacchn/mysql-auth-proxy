package me.isaac.audit.protocol_v2.basic_type;

import cn.hutool.core.util.ArrayUtil;
import io.netty.buffer.ByteBuf;
import me.isaac.audit.protocol.util.CustomByteUtil;
import me.isaac.audit.util.NumberUtil;

public class EncodedLengthString extends MysqlBasicData {
    @Override
    public void readFrom(ByteBuf buf) {
        //首先计算长度
        int first = CustomByteUtil.byteToInt(buf.readByte()); //todo 重构优化逻辑
        byte[] encodedLengthBytes;
        if (first < 0xfb) {
            encodedLengthBytes = new byte[1];
            encodedLengthBytes[0] = CustomByteUtil.intToByte(first);
        } else if (first == 0xfc) {
            encodedLengthBytes = new byte[3];
            encodedLengthBytes[0] = CustomByteUtil.intToByte(first);
            buf.readBytes(encodedLengthBytes, 1, 2);
        } else if (first == 0xfd) {
            encodedLengthBytes = new byte[4];
            encodedLengthBytes[0] = CustomByteUtil.intToByte(first);
            buf.readBytes(encodedLengthBytes, 1, 3);
        } else if (first == 0xfe) {
            encodedLengthBytes = new byte[9];
            encodedLengthBytes[0] = CustomByteUtil.intToByte(first);
            buf.readBytes(encodedLengthBytes, 1, 8);
        } else {
            //todo
            encodedLengthBytes = new byte[1];
        }
        int length = NumberUtil.lengthEncodedBytesToInt(encodedLengthBytes); //字符串的长度
        byte[] strBytes = new byte[length];
        buf.readBytes(strBytes);
        this.bytes = ArrayUtil.addAll(encodedLengthBytes, strBytes);
    }
}
