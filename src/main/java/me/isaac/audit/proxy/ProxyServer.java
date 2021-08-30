package me.isaac.audit.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.isaac.audit.protocol_v3.codec.MySQLPacketCodecEngine;
import me.isaac.audit.protocol_v3.codec.PacketCodec;

import java.net.InetSocketAddress;

public class ProxyServer {
    private final int port;

    public ProxyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        final ProxyServerHandler serverHandler = new ProxyServerHandler("10.10.20.90", 3306);
        EventLoopGroup masterGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(masterGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                           // ch.pipeline().addLast(new PacketCodec(new MySQLPacketCodecEngine()));
                            ch.pipeline().addLast(serverHandler);
                        }
                    }).childOption(ChannelOption.AUTO_READ, false);
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            masterGroup.shutdownGracefully().sync();
        }
    }
}
