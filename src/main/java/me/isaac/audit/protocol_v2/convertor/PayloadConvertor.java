package me.isaac.audit.protocol_v2.convertor;

import io.netty.buffer.ByteBuf;
import me.isaac.audit.protocol_v2.basic_type.*;
import me.isaac.audit.protocol_v2.payload.Payload;

import java.util.LinkedHashMap;

/**
 * 将ByteBuf转换为Payload
 */
public class PayloadConvertor {
    private ByteBuf buf;

    public PayloadConvertor(ByteBuf buf) {
        this.buf = buf;
    }

    public Payload createFromFormat(LinkedHashMap<String, String> map) {
        Payload payload = new Payload();
        map.forEach((k, v) -> {
            buildPayload(payload, k, v);
        });
        return payload;//todo 解析为Payload
    }

    private void buildPayload(Payload payload, String key, String dataTypeStr) {
        MysqlBasicData data = null;
        //解析value的格式
        if (MysqlDataTypeUtil.isFixedLengthInt(dataTypeStr)) {
            int length = MysqlDataTypeUtil.getIntFixedLength(dataTypeStr);//int长度
            data = new FixedLengthInt(length);
            data.readFrom(buf);
        } else if (MysqlDataTypeUtil.isEncodedLengthInt(dataTypeStr)) {
            data = new EncodedLengthInt();
            data.readFrom(buf);
        } else if (MysqlDataTypeUtil.isFixedLengthString(dataTypeStr)) {
            int length = MysqlDataTypeUtil.getStringFixedLength(dataTypeStr);
            data = new FixedLengthString(length);
            data.readFrom(buf);
        } else if (MysqlDataTypeUtil.isEncodedLengthString(dataTypeStr)) {
            data = new EncodedLengthString();
            data.readFrom(buf);
        } else if (MysqlDataTypeUtil.isVariableLengthString(dataTypeStr)) {
            String mapK = MysqlDataTypeUtil.getStringVariableKey(dataTypeStr);//长度变量的字段
            int strLen = ((IntType) payload.getAttribute(mapK)).intValue();
            data = new FixedLengthString(strLen);
            data.readFrom(buf);
        } else if (MysqlDataTypeUtil.isEofString(dataTypeStr)) {
            data = new EofString();
            data.readFrom(buf);
        } else if (MysqlDataTypeUtil.isNullTerminatedString(dataTypeStr)) {
            data = new NullTerminatedString();
            data.readFrom(buf);
        } else {
            //todo 报错
        }
        payload.addAttribute(key, data);
    }

    private void buildData(Payload payload, String key, String dataType) {

    }

}
