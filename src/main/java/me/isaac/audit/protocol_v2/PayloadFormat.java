package me.isaac.audit.protocol_v2;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Payload数据包的类型
 */
public class PayloadFormat {
    static Map<String, String> map;

    static {
        map = new LinkedHashMap<>();
        map.put("protocol_version", "int<1>");
        map.put("server_version", "string<null>");
        map.put("connection_id", "int<4>");
        map.put("auth_plugin_data_part_1", "string<8>");
        map.put("filler_1", "int<1>");
        map.put("auth-plugin-data", "null,[(capability_low,CLIENT_PLUGIN_AUTH,int<1>),()]");
    }
}
