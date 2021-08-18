package me.isaac.audit.protocol_v2.payload;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Payload数据包的类型
 */
public class PayloadFormatConstants {
    //int<[123468]>
    //int<lenenc>
    //string<lenenc>
    //string<[0-9]+>
    //string<var>,filler_1
    //string<EOF>
    //string<NUL>


    static Map<String, String> map;

    static {
        map = new LinkedHashMap<>();
        map.put("protocol_version", "int<1>");
        map.put("server_version", "string<NUL>");
        map.put("connection_id", "int<4>");
        map.put("auth_plugin_data_part_1", "string<fix>");
        map.put("filler_1", "int<1>");

        map.put("auth-plugin-data", "conditional,[(capability_low,CLIENT_PLUGIN_AUTH,int<1>),()]");
    }
}
