package me.isaac.audit.protocol_v2.payload;

import me.isaac.audit.protocol_v2.basic_type.MysqlBasicData;

import java.util.LinkedHashMap;
import java.util.Map;

public class Payload {
    private Map<String, MysqlBasicData> map;
    private String payloadType;//类型

    public Payload() {
        this.map = new LinkedHashMap<>();
    }

    private void addAttribute(String name, MysqlBasicData data) {
        this.map.put(name, data);
    }

    private MysqlBasicData getAttribute(String name) {
        return this.map.get(name);
    }
}
