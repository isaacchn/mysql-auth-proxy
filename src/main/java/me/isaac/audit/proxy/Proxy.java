package me.isaac.audit.proxy;

public class Proxy {
    public static void main(String[] args) throws Exception {
        new ProxyServer(9000).start();
    }
}
