package me.isaac.audit.protocol.util;

public class FixedLengthUtil {
    /**
     * @see https://dev.mysql.com/doc/internals/en/integer.html#packet-Protocol::FixedLengthInteger
     */
    public static int readUB3(byte[] b) {
        //todo b.length == 3
        int first = byteToInt(b[0]);
        if (first <= 0xfa) {
            return first;
        } else if (first == 0xfb) {
            return 0;
        } else if (first == 0xfc) {
            return byteToInt(b[1]);
        } else if (first == 0xfd) {
            return byteToInt(b[1]) << 8 | byteToInt(b[2]);
        }
        return 0;
    }

    public static int readUB1(byte b) {
        return byteToInt(b);
    }

    private static int byteToInt(byte b) {
        return b & 0xff;
    }
}
