package me.isaac.audit.protocol.util;

/**
 * 与cn.hutool.core.util.ByteUtil区分
 */
public class CustomByteUtil {
    //byte 与 int 的相互转换
    public static byte intToByte(int x) {
        return (byte) x;
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    //将int数组转为byte数组
    //数组元素的值应当在0-255之间
    public static byte[] intArrToByteArr(int[] arr) {
        byte[] result = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = intToByte(arr[i]);
        }
        return result;
    }

    //将byte数组转为int数组
    public static int[] byteArrToIntArr(byte[] arr) {
        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = byteToInt(arr[i]);
        }
        return result;
    }
}
