package me.isaac.audit.protocol.pack;

import lombok.Data;

/**
 * @see https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::HandshakeResponse
 */
@Data
public class AuthPayload extends Payload {
    public long clientCapability; //客户端权能标志 4字节
    public long maxPacketSize; //最大消息长度 4字节
    public int charset; //字符编码
    public byte[] reserved;//填充值 0x00 23字节
    public String user; //用户 NULL-Terminated
    public int authResponseLength; //挑战认证数据长度
    public String authResponse; //挑战认证数据
    public String database; //数据库名称(可选) NULL-Terminated
    public String authPluginName; //auth-plugin name
    public String connectAttrs; //其他连接参数
}
