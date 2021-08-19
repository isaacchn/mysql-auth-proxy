package me.isaac.audit.protocol_v3.base;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.ByteBuf;

public final class MySQLPayload implements DbPayload {
    private final ByteBuf byteBuf;

    public MySQLPayload(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    /**
     * Read 1 byte fixed length integer from byte buffers.
     *
     * @return 1 byte fixed length integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger">FixedLengthInteger</a>
     */
    public int readInt1() {
        return byteBuf.readUnsignedByte();
    }

    /**
     * Write 1 byte fixed length integer to byte buffers.
     *
     * @param value 1 byte fixed length integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger">FixedLengthInteger</a>
     */
    public void writeInt1(final int value) {
        byteBuf.writeByte(value);
    }

    /**
     * Read 2 byte fixed length integer from byte buffers.
     *
     * @return 2 byte fixed length integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger">FixedLengthInteger</a>
     */
    public int readInt2() {
        return byteBuf.readUnsignedShortLE();
    }

    /**
     * Write 2 byte fixed length integer to byte buffers.
     *
     * @param value 2 byte fixed length integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger">FixedLengthInteger</a>
     */
    public void writeInt2(final int value) {
        byteBuf.writeShortLE(value);
    }

    /**
     * Read 3 byte fixed length integer from byte buffers.
     *
     * @return 3 byte fixed length integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger">FixedLengthInteger</a>
     */
    public int readInt3() {
        return byteBuf.readUnsignedMediumLE();
    }

    /**
     * Write 3 byte fixed length integer to byte buffers.
     *
     * @param value 3 byte fixed length integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger">FixedLengthInteger</a>
     */
    public void writeInt3(final int value) {
        byteBuf.writeMediumLE(value);
    }

    /**
     * Read 4 byte fixed length integer from byte buffers.
     *
     * @return 4 byte fixed length integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger">FixedLengthInteger</a>
     */
    public int readInt4() {
        return byteBuf.readIntLE();
    }

    /**
     * Write 4 byte fixed length integer to byte buffers.
     *
     * @param value 4 byte fixed length integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger">FixedLengthInteger</a>
     */
    public void writeInt4(final int value) {
        byteBuf.writeIntLE(value);

    }

    /**
     * Read 6 byte fixed length integer from byte buffers.
     *
     * @return 6 byte fixed length integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger">FixedLengthInteger</a>
     */
    public long readInt6() {
        long result = 0;
        for (int i = 0; i < 6; i++) {
            result |= ((long) (0xff & byteBuf.readByte())) << (8 * i);
        }
        return result;
    }

    /**
     * Write 6 byte fixed length integer to byte buffers.
     *
     * @param value 6 byte fixed length integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger">FixedLengthInteger</a>
     */
    public void writeInt6(final int value) {
        // TODO
    }

    /**
     * Read 8 byte fixed length integer from byte buffers.
     *
     * @return 8 byte fixed length integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger">FixedLengthInteger</a>
     */
    public long readInt8() {
        return byteBuf.readLongLE();
    }

    /**
     * Write 8 byte fixed length integer to byte buffers.
     *
     * @param value 8 byte fixed length integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger">FixedLengthInteger</a>
     */
    public void writeInt8(final long value) {
        byteBuf.writeLongLE(value);

    }

    /**
     * Read lenenc integer from byte buffers.
     *
     * @return lenenc integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::LengthEncodedInteger">LengthEncodedInteger</a>
     */
    public long readIntLenenc() {
        int firstByte = readInt1();
        if (firstByte < 0xfb) {
            return firstByte;
        }
        if (0xfb == firstByte) {
            return 0;
        }
        if (0xfc == firstByte) {
            return readInt2();
        }
        if (0xfd == firstByte) {
            return readInt3();
        }
        return byteBuf.readLongLE();
    }

    /**
     * Write lenenc integer to byte buffers.
     *
     * @param value lenenc integer
     * @see <a href="https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::LengthEncodedInteger">LengthEncodedInteger</a>
     */
    public void writeIntLenenc(final long value) {
        if (value < 0xfb) {
            byteBuf.writeByte((int) value);
            return;
        }
        if (value < Math.pow(2, 16)) {
            byteBuf.writeByte(0xfc);
            byteBuf.writeShortLE((int) value);
            return;
        }
        if (value < Math.pow(2, 24)) {
            byteBuf.writeByte(0xfd);
            byteBuf.writeMediumLE((int) value);
            return;
        }
        byteBuf.writeByte(0xfe);
        byteBuf.writeLongLE(value);
    }

    /**
     * Read fixed length long from byte buffers.
     *
     * @param length length read from byte buffers
     * @return fixed length long
     */
    public long readLong(final int length) {
        long result = 0;
        for (int i = 0; i < length; i++) {
            result = result << 8 | readInt1();
        }
        return result;
    }

    /**
     * Read lenenc string from byte buffers.
     *
     * @return lenenc string
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::FixedLengthString">FixedLengthString</a>
     */
    public String readStringLenenc() {
        int length = (int) readIntLenenc();
        byte[] result = new byte[length];
        byteBuf.readBytes(result);
        return new String(result);
    }

    /**
     * Read lenenc string from byte buffers for bytes.
     *
     * @return lenenc bytes
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::FixedLengthString">FixedLengthString</a>
     */
    public byte[] readStringLenencByBytes() {
        int length = (int) readIntLenenc();
        byte[] result = new byte[length];
        byteBuf.readBytes(result);
        return result;
    }

    /**
     * Write lenenc string to byte buffers.
     *
     * @param value fixed length string
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::FixedLengthString">FixedLengthString</a>
     */
    public void writeStringLenenc(final String value) {
        if (StrUtil.isEmpty(value)) {
            byteBuf.writeByte(0);
            return;
        }
        writeIntLenenc(value.getBytes().length);
        byteBuf.writeBytes(value.getBytes());
    }

    /**
     * Write lenenc bytes to byte buffers.
     *
     * @param value fixed length bytes
     */
    public void writeBytesLenenc(final byte[] value) {
        if (0 == value.length) {
            byteBuf.writeByte(0);
            return;
        }
        writeIntLenenc(value.length);
        byteBuf.writeBytes(value);
    }

    /**
     * Read fixed length string from byte buffers.
     *
     * @param length length of fixed string
     * @return fixed length string
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::FixedLengthString">FixedLengthString</a>
     */
    public String readStringFix(final int length) {
        byte[] result = new byte[length];
        byteBuf.readBytes(result);
        return new String(result);
    }

    /**
     * Read fixed length string from byte buffers and return bytes.
     *
     * @param length length of fixed string
     * @return fixed length bytes
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::FixedLengthString">FixedLengthString</a>
     */
    public byte[] readStringFixByBytes(final int length) {
        byte[] result = new byte[length];
        byteBuf.readBytes(result);
        return result;
    }

    /**
     * Write variable length string to byte buffers.
     *
     * @param value fixed length string
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::FixedLengthString">FixedLengthString</a>
     */
    public void writeStringFix(final String value) {
        byteBuf.writeBytes(value.getBytes());
    }

    /**
     * Write variable length bytes to byte buffers.
     *
     * @param value fixed length bytes
     * @see <a href="https://dev.mysql.com/doc/internals/en/secure-password-authentication.html#packet-Authentication::Native41">Native41</a>
     */
    public void writeBytes(final byte[] value) {
        byteBuf.writeBytes(value);
    }

    /**
     * Read variable length string from byte buffers.
     *
     * @return variable length string
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::VariableLengthString">FixedLengthString</a>
     */
    public String readStringVar() {
        // TODO
        return "";
    }

    /**
     * Write fixed length string to byte buffers.
     *
     * @param value variable length string
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::VariableLengthString">FixedLengthString</a>
     */
    public void writeStringVar(final String value) {
        // TODO
    }

    /**
     * Read null terminated string from byte buffers.
     *
     * @return null terminated string
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::NulTerminatedString">NulTerminatedString</a>
     */
    public String readStringNul() {
        byte[] result = new byte[byteBuf.bytesBefore((byte) 0)];
        byteBuf.readBytes(result);
        byteBuf.skipBytes(1);
        return new String(result);
    }

    /**
     * Read null terminated string from byte buffers and return bytes.
     *
     * @return null terminated bytes
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::NulTerminatedString">NulTerminatedString</a>
     */
    public byte[] readStringNulByBytes() {
        byte[] result = new byte[byteBuf.bytesBefore((byte) 0)];
        byteBuf.readBytes(result);
        byteBuf.skipBytes(1);
        return result;
    }

    /**
     * Write null terminated string to byte buffers.
     *
     * @param value null terminated string
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::NulTerminatedString">NulTerminatedString</a>
     */
    public void writeStringNul(final String value) {
        byteBuf.writeBytes(value.getBytes());
        byteBuf.writeByte(0);
    }

    /**
     * Read rest of packet string from byte buffers and return bytes.
     *
     * @return rest of packet string bytes
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::RestOfPacketString">RestOfPacketString</a>
     */
    public byte[] readStringEOFByBytes() {
        byte[] result = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(result);
        return result;
    }

    /**
     * Read rest of packet string from byte buffers.
     *
     * @return rest of packet string
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::RestOfPacketString">RestOfPacketString</a>
     */
    public String readStringEOF() {
        byte[] result = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(result);
        return new String(result);
    }

    /**
     * Write rest of packet string to byte buffers.
     *
     * @param value rest of packet string
     * @see <a href="https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::RestOfPacketString">RestOfPacketString</a>
     */
    public void writeStringEOF(final String value) {
        byteBuf.writeBytes(value.getBytes());
    }

    /**
     * Skip reserved from byte buffers.
     *
     * @param length length of reserved
     */
    public void skipReserved(final int length) {
        byteBuf.skipBytes(length);
    }

    /**
     * Write null for reserved to byte buffers.
     *
     * @param length length of reserved
     */
    public void writeReserved(final int length) {
        for (int i = 0; i < length; i++) {
            byteBuf.writeByte(0);
        }
    }

    @Override
    public void close() {
        byteBuf.release();
    }
}
