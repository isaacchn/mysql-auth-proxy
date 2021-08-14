package me.isaac.audit.protocol_v2.format;

/**
 * 将字节数组格式化输出
 */
public interface IFormatter {
    String formattedValue(byte[] bytes);
}
