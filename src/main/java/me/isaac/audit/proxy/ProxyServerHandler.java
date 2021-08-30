package me.isaac.audit.proxy;

import cn.hutool.json.JSONUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import me.isaac.audit.protocol_v3.base.MySQLPayload;
import me.isaac.audit.protocol_v3.packet.handshake.HandshakeResponse41Packet;
import me.isaac.audit.util.ClientUtil;

import java.net.InetSocketAddress;

/**
 * 接收客户端请求，解析并发送给MySQL服务端
 */
@ChannelHandler.Sharable
@Slf4j
public class ProxyServerHandler extends ChannelInboundHandlerAdapter {
    private final String remoteHost;
    private final int remotePort;

    final static int SEQUENCE_LENGTH = 1; //sequence id length of mysql packet

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
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // ch.pipeline().addLast(new PacketCodec(new MySQLPacketCodecEngine()));
                        ch.pipeline().addLast(new ProxyClientHandler(serverChannel));
                    }
                })
                // .handler(new ProxyClientHandler(serverChannel))
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
            ByteBuf in = (ByteBuf) msg;
            log.debug("ProxyServerHandler.channelRead() channel_id={} readable_bytes={}", ctx.channel().id().toString(), in.readableBytes());
            System.out.println("========== Server Received ==========");
            //获取TCP连接IP及端口信息
            InetSocketAddress proxyAddress = (InetSocketAddress) ctx.channel().localAddress();
            InetSocketAddress clientAddress = (InetSocketAddress) ctx.channel().remoteAddress();

            System.out.printf("接收到客户端响应 client_addr=%s client_port=%d proxy_addr=%s proxy_port=%d%n",
                    clientAddress.getAddress().getHostAddress(), clientAddress.getPort(),
                    proxyAddress.getAddress().getHostAddress(), proxyAddress.getPort());

            System.out.println(ByteBufUtil.prettyHexDump(in));

            //---------- 分析报文
            /* 02 客户端 -> 服务端 登录认证 */
            if (ClientUtil.isAfterHandshake(clientAddress)) {
                System.out.println("这是一条登录认证报文");
                ClientUtil.sendAuth(clientAddress);
                ByteBuf copiedBuf = Unpooled.copiedBuffer(in);//复制 todo 可能存在性能隐患
                int payloadLength = copiedBuf.markReaderIndex().readMediumLE();
                log.debug("payload_length={}", payloadLength);
                HandshakeResponse41Packet packet = new HandshakeResponse41Packet(new MySQLPayload(copiedBuf.readRetainedSlice(SEQUENCE_LENGTH + payloadLength)));
                log.debug("handshake_packet: \n{}", JSONUtil.parse(packet).toJSONString(2));
            } else {

            }

            //System.out.println(in.toString(CharsetUtil.UTF_8));
            System.out.println(ByteBufUtil.prettyHexDump(in));
            // System.out.println("========== End of Server Received ==========");
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
