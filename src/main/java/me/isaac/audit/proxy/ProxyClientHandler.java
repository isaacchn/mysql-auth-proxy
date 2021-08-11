package me.isaac.audit.proxy;

import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import me.isaac.audit.protocol.pack.HandshakePayload;
import me.isaac.audit.protocol.pack.MySQLPacket;
import me.isaac.audit.protocol.util.PacketConvertUtil;
import me.isaac.audit.util.ClientUtil;

import java.net.InetSocketAddress;

@ChannelHandler.Sharable
public class ProxyClientHandler extends ChannelInboundHandlerAdapter {
    private final Channel serverChannel;

    public ProxyClientHandler(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("========== Client Received ==========");
        //获取TCP连接IP及端口信息
        InetSocketAddress proxyAddress = (InetSocketAddress) ctx.channel().localAddress();
        InetSocketAddress mysqlAddress = (InetSocketAddress) ctx.channel().remoteAddress();

        System.out.printf("接收到MySQL服务端响应 mysql_addr=%s mysql_port=%d proxy_addr=%s proxy_port=%d%n",
                mysqlAddress.getAddress().getHostAddress(), mysqlAddress.getPort(),
                proxyAddress.getAddress().getHostAddress(), proxyAddress.getPort());

        InetSocketAddress clientAddress = (InetSocketAddress) serverChannel.remoteAddress();
        System.out.printf("对应客户端信息 client_addr=%s client_port=%d%n",
                clientAddress.getAddress().getHostAddress(),
                clientAddress.getPort());
        //---------- 分析报文
        /* 01 服务端->客户端 握手初始化 */
        if (ClientUtil.isNew(clientAddress)) {
            System.out.println("这是一条握手初始化报文");
            ClientUtil.init((InetSocketAddress) serverChannel.remoteAddress());
            MySQLPacket<HandshakePayload> packet = PacketConvertUtil.convertToHandshake(in);
            System.out.println(JSONUtil.parse(packet).toJSONString(2));
        } else {

        }

        System.out.println(in.toString(CharsetUtil.UTF_8));
        System.out.println(ByteBufUtil.prettyHexDump(in));
        System.out.println("========== End of Client Received ==========");
        serverChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
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

//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
//       // System.out.println("Client received: " + msg.toString(CharsetUtil.UTF_8));
//        serverChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                if (future.isSuccess()) {
//                    ctx.channel().read();
//                } else {
//                    future.channel().close();
//                }
//            }
//        });
//    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ProxyServerHandler.closeOnFlush(serverChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ProxyServerHandler.closeOnFlush(ctx.channel());
    }
}