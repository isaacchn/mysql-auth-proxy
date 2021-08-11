package me.isaac.audit.util;

import cn.hutool.core.util.ObjectUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class ClientUtil {
    /**
     * 1 = 服务端->客户端 握手初始化
     * 2 = 客户端 -> 服务端 登录认证
     * 3 = 服务端 -> 客户端 认证结果
     */
    private static final ConcurrentHashMap<String, Integer> MAP = new ConcurrentHashMap<>();

    public static boolean isNew(InetSocketAddress address) {
        String key = addrToKey(address);
        return ObjectUtil.isNull(MAP.get(key));
    }

    public static boolean afterHandshake(InetSocketAddress address) {
        String key = addrToKey(address);
        return MAP.get(key) == 1;
    }

    /**
     * 客户端向服务端发送完成认证信息
     */
    public static void afterSendAuth(InetSocketAddress address) {
        String key = addrToKey(address);
        MAP.put(key, 2);
    }

    public static void init(InetSocketAddress address) {
        String key = addrToKey(address);
        MAP.put(key, 1);
    }

    private static String addrToKey(InetSocketAddress address) {
        String ip = address.getAddress().getHostAddress();
        int port = address.getPort();
        return String.format("%s:%d", ip, port);
    }
}
