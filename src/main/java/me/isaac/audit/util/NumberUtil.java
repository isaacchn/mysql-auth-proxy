package me.isaac.audit.util;

import cn.hutool.core.util.ArrayUtil;
import me.isaac.audit.protocol.util.CustomByteUtil;

/**
 * 将小端字节序字节数组转为Long
 */
public class NumberUtil {
    public static long bytesToLong(byte[] bytes) {
        if (bytes.length > 8 || bytes.length == 0) {
            //todo
            return 0;
        } else {
            long result = 0;
            for (byte aByte : bytes) {
                result = result << 8;
                result |= aByte;
            }
            return result;
        }
    }

    public static int bytesToInt(byte[] bytes) {
        return (int) bytesToLong(bytes);//todo 这里的逻辑是否正确？
    }

    public static long lengthEncodedBytesToLong(byte[] bytes) {
        if (bytes.length > 9 || bytes.length == 0) {
            //todo
            return 0;
        }
        int first = CustomByteUtil.byteToInt(bytes[0]);
        if (first < 0xfb) {
            return first;
        } else if (first == 0xfc) {
            byte[] newBytes = new byte[2];
            ArrayUtil.copy(bytes, 1, newBytes, 0, 2);
            return bytesToLong(newBytes);
        } else if (first == 0xfd) {
            byte[] newBytes = new byte[3];
            ArrayUtil.copy(bytes, 1, newBytes, 0, 3);
            return bytesToLong(newBytes);
        } else if (first == 0xfe) {
            byte[] newBytes = new byte[8];
            ArrayUtil.copy(bytes, 1, newBytes, 0, 8);
            return bytesToLong(newBytes);
        } else {
            //todo 报错
            return 0;
        }
    }

    public static int lengthEncodedBytesToInt(byte[] bytes) {
        long l = lengthEncodedBytesToLong(bytes);
        return (int) l;
    }
}
