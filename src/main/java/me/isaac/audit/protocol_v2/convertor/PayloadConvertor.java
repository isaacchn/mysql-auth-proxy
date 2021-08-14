package me.isaac.audit.protocol_v2.convertor;

import io.netty.buffer.ByteBuf;
import me.isaac.audit.protocol_v2.payload.Payload;

import java.util.Map;

/**
 * 将ByteBuf转换为Payload
 */
public class PayloadConvertor {
    private ByteBuf buf;

    public PayloadConvertor(ByteBuf buf) {
        this.buf = buf;
    }

    public Payload createFromFormat(Map<String, String> map) {
        map.forEach((k, v) -> {
            //k=field
            //v=格式
        });
        return null;//todo 解析为Payload
    }


}
