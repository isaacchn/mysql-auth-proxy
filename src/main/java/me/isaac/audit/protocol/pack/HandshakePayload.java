package me.isaac.audit.protocol.pack;

import lombok.Data;

/**
 * @see https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::Handshake
 * (1)
 * 0a protocolVersion
 * (2)
 * 35 2e 36 2e 35 30 2d 6c 6f 67 00
 * string[NUL]    serverVersion 10字节 00是填充位
 * 5.6.50-log
 * (3)
 * 1e b0 0c 00 connectionId
 * (4)
 * 2f 46 54 55 2e 23 62 45
 * /FTU.#bE
 * 挑战随机数(auth-plugin-data-part-1) 8字节 authDataPart1
 * (5)
 * 00 filler1
 * (6)
 * ff f7 服务器权能标志(低16位) 2字节 capabilityLow
 * (7)
 * 2e 字符编码 1字节 serverCharset
 * (8)
 * 02 00 服务器状态 2字节
 * (9)
 * 7f 80 capabilityHigh 服务器权能标志(高16位) 2字节
 * (10)
 * 15
 * authPluginLen; //00或length of auth-plugin-data 1字节
 * (11)
 * 00 00 00 00 00 00 00 00 00 00 reserved
 * (12)
 * 73 36 51 73 70 57 46 35 76 74 63 75 00 6d 79 73 00 00是填充位
 * s6QspWF5vtcu  auth-plugin-data-part-2
 * (13)
 * 6d 79 73 71 6c 5f 6e 61 74 69 76 65 5f 70 61 73 73 77 6f 72 64 21字节
 * mysql_native_password
 * (14)
 * 00填充
 */

@Data
public class HandshakePayload extends Payload {
    public int protocolVersion; //协议版本号 1字节 0x0a
    public String serverVersion; //服务器版本信息 N字节
    public long connectionId; //服务器线程ID 4字节
    public String authDataPart1; //挑战随机数(auth-plugin-data-part-1) 8字节
    public int filler1;//填充值 0x00 1字节
    public int capabilityLow; //服务器权能标志(低16位) 2字节
    public int charset; //字符编码 1字节
    public int serverStatus; //服务器状态 2字节
    public int capabilityHigh; //服务器权能标志(高16位) 2字节
    public int authPluginLen; //00或length of auth-plugin-data 1字节
    public int filler2;//填充值 0x00 1字节
    private String authDataPart2; //挑战随机数 $len=MAX(13, authPluginLen - 8)
    private String authPluginName; //auth-plugin name
}
