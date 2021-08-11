package me.isaac.audit.protocol.pack;

public enum PayloadType {
    HANDSHAKE(1, "握手初始化报文"),
    AUTH_LOGIN(2, "登陆认证报文"),
    CLIENT_COM_QUIT(0x01, "COM_QUIT 关闭连接");
    private int id;
    private String remark;

    private PayloadType(int id, String remark) {
        this.id = id;
        this.remark = remark;
    }
}
