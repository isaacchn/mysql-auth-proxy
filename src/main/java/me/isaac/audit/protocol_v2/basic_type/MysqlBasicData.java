package me.isaac.audit.protocol_v2.basic_type;

import io.netty.buffer.ByteBuf;

/**
 * MySQL内置数据类型
 * Type	Description
 * int<1>	        1 byte Protocol::FixedLengthInteger
 * int<2>	        2 byte Protocol::FixedLengthInteger
 * int<3>	        3 byte Protocol::FixedLengthInteger
 * int<4>	        4 byte Protocol::FixedLengthInteger
 * int<6>	        6 byte Protocol::FixedLengthInteger
 * int<8>	        8 byte Protocol::FixedLengthInteger
 * int<lenenc>	    Protocol::LengthEncodedInteger
 * string<lenenc>	Protocol::LengthEncodedString
 * string<fix>	    Protocol::FixedLengthString
 * string<var>	    Protocol::VariableLengthString - The length of the string is determined by another field or is calculated at runtime
 * string<EOF>	    Protocol::RestOfPacketString - The last component of a packet
 * string<NUL>	    Protocol::NulTerminatedString
 */
public abstract class MysqlBasicData {
    protected byte[] bytes;

//    public MysqlBasicData(byte[] bytes) {
//        this.bytes = bytes;
//    }

    //从buffer中读取数据，同时移动游标的位置
    public abstract void readFrom(ByteBuf buf);

    //
//    public byte[] bytesValue() {
//        return this.bytes;
//    }
//
    //public abstract String formatValue();
    public int getLength() {
        return bytes.length;
    }
}
