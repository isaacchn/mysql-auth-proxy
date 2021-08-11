package me.isaac.audit.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import me.isaac.audit.util.ClientUtil;

import java.net.InetSocketAddress;

@ChannelHandler.Sharable
public class ProxyServerHandler extends ChannelInboundHandlerAdapter {
    private final String remoteHost;
    private final int remotePort;

    private Channel clientChannel;

    public ProxyServerHandler(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel serverChannel = ctx.channel();
        Bootstrap b = new Bootstrap();
        b.group(serverChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new ProxyClientHandler(serverChannel))
                .option(ChannelOption.AUTO_READ, false);
        ChannelFuture f = b.connect(remoteHost, remotePort);
        clientChannel = f.channel();
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    serverChannel.read();
                } else {
                    serverChannel.close();
                }
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //todo 获取请求来源IP和端口，判断是否存在该连接。
        if (clientChannel.isActive()) {
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println("========== Server Received ==========");
            //获取TCP连接IP及端口信息
            InetSocketAddress proxyAddress = (InetSocketAddress) ctx.channel().localAddress();
            InetSocketAddress clientAddress = (InetSocketAddress) ctx.channel().remoteAddress();

            System.out.printf("接收到客户端响应 client_addr=%s client_port=%d proxy_addr=%s proxy_port=%d%n",
                    clientAddress.getAddress().getHostAddress(), clientAddress.getPort(),
                    proxyAddress.getAddress().getHostAddress(), proxyAddress.getPort());

            //---------- 分析报文
            /* 02 客户端 -> 服务端 登录认证 */
            if (ClientUtil.afterHandshake(clientAddress)) {
                System.out.println("这是一条登录认证报文");
                ClientUtil.afterSendAuth(clientAddress);

            }

            System.out.println(byteBuf.toString(CharsetUtil.UTF_8));
            System.out.println(ByteBufUtil.prettyHexDump(byteBuf));
            System.out.println("========== End of Server Received ==========");
            clientChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (clientChannel != null) {
            closeOnFlush(clientChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
