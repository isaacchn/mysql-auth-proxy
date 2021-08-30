package me.isaac.audit.proxy;

import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import me.isaac.audit.protocol.util.CustomByteUtil;
import me.isaac.audit.protocol_v3.base.MySQLPayload;
import me.isaac.audit.protocol_v3.packet.generic.MySQLEofPacket;
import me.isaac.audit.protocol_v3.packet.generic.MySQLErrPacket;
import me.isaac.audit.protocol_v3.packet.generic.MySQLOKPacket;
import me.isaac.audit.protocol_v3.packet.handshake.HandshakePacket;
import me.isaac.audit.util.ClientUtil;

import java.net.InetSocketAddress;

@ChannelHandler.Sharable
@Slf4j
public class ProxyClientHandler extends ChannelInboundHandlerAdapter {
    private final Channel serverChannel;
    final static int SEQUENCE_LENGTH = 1; //sequence id length of mysql packet

    public ProxyClientHandler(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    //todo 对报错进行处理
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        Channel clientChannel = ctx.channel();
        log.debug("ProxyClientHandler.channelRead() channel_id={} readable_bytes={}", ctx.channel().id().toString(), in.readableBytes());
        System.out.println("========== Proxy Client Received ==========");
        //获取TCP连接IP及端口信息
        InetSocketAddress proxyAddress = (InetSocketAddress) clientChannel.localAddress();
        InetSocketAddress mysqlAddress = (InetSocketAddress) clientChannel.remoteAddress();

        System.out.printf("接收到MySQL服务端响应 mysql_addr=%s mysql_port=%d proxy_addr=%s proxy_port=%d%n",
                mysqlAddress.getAddress().getHostAddress(), mysqlAddress.getPort(),
                proxyAddress.getAddress().getHostAddress(), proxyAddress.getPort());

        InetSocketAddress clientAddress = (InetSocketAddress) serverChannel.remoteAddress();
        System.out.printf("对应客户端信息 client_addr=%s client_port=%d%n",
                clientAddress.getAddress().getHostAddress(),
                clientAddress.getPort());

        //System.out.println(in.toString(CharsetUtil.UTF_8));
        System.out.println(ByteBufUtil.prettyHexDump(in));
        //---------- 分析报文
        /* 01 服务端->客户端 握手初始化 */
        if (ClientUtil.isNew(clientAddress)) {
            System.out.println("这是一条握手初始化报文");
            ClientUtil.handshake(clientAddress);
            // MySQLPacket packet = new HandshakePacket(new MySQLPayload(in));
            //System.out.println(JSONUtil.parse(packet).toJSONString(2));
//            int readableBytes = in.readableBytes();
//            MySQLCodecEngine codecEngine = new MySQLCodecEngine();
            ByteBuf copiedBuf = Unpooled.copiedBuffer(in);//复制 todo 可能存在性能隐患
            int payloadLength = copiedBuf.markReaderIndex().readMediumLE();
            log.debug("payload_length={}", payloadLength);
            HandshakePacket packet = new HandshakePacket(new MySQLPayload(copiedBuf.readRetainedSlice(SEQUENCE_LENGTH + payloadLength)));
            log.debug("handshake_packet: \n{}", JSONUtil.parse(packet).toJSONString(2));
        }
        /* 03 服务端->客户端 认证响应 */
        else if (ClientUtil.isAfterSendAuth(clientAddress)) {
            log.debug("接收到服务端认证返回信息");
            //ClientUtil.recvAuth(clientAddress);
            ByteBuf copiedBuf = Unpooled.copiedBuffer(in);//复制 todo 可能存在性能隐患
            int payloadLength = copiedBuf.markReaderIndex().readMediumLE();
            log.debug("payload_length={}", payloadLength);
            int flag = CustomByteUtil.byteToInt(copiedBuf.getByte(4));//标志位
            if (flag == 0x00 || flag == 0xFE) {
                ClientUtil.authOk(clientAddress);
                MySQLOKPacket packet = new MySQLOKPacket(new MySQLPayload(copiedBuf.readRetainedSlice(SEQUENCE_LENGTH + payloadLength)));
                log.debug("resp_packet: \n{}", JSONUtil.parse(packet).toJSONString(2));
            } else if (flag == 0xFF) {
                ClientUtil.authFailed(clientAddress);
                MySQLErrPacket packet = new MySQLErrPacket(new MySQLPayload(copiedBuf.readRetainedSlice(SEQUENCE_LENGTH + payloadLength)));
                log.debug("resp_packet: \n{}", JSONUtil.parse(packet).toJSONString(2));
            } else {
                //todo 异常
            }
        }
        /* 服务端->客户端 返回结果 */
        else if (ClientUtil.isAfterAuthOk(clientAddress)) {
            log.debug("接收到服务端返回的结果");
            ByteBuf copiedBuf = Unpooled.copiedBuffer(in);//复制 todo 可能存在性能隐患  todo 重复的代码
            int payloadLength = copiedBuf.markReaderIndex().readMediumLE();
            log.debug("payload_length={}", payloadLength);
            int flag = CustomByteUtil.byteToInt(copiedBuf.getByte(4));//标志位
            if (flag == 0x00) {
                //ClientUtil.authOk(clientAddress);
                MySQLOKPacket packet = new MySQLOKPacket(new MySQLPayload(copiedBuf.readRetainedSlice(SEQUENCE_LENGTH + payloadLength)));
                log.debug("resp_packet: \n{}", JSONUtil.parse(packet).toJSONString(2));
            } else if (flag == 0xFF) {
               // ClientUtil.authFailed(clientAddress);
                MySQLErrPacket packet = new MySQLErrPacket(new MySQLPayload(copiedBuf.readRetainedSlice(SEQUENCE_LENGTH + payloadLength)));
                log.debug("resp_packet: \n{}", JSONUtil.parse(packet).toJSONString(2));
            } else if (flag == 0xFE) {
                MySQLEofPacket packet = new MySQLEofPacket(new MySQLPayload(copiedBuf.readRetainedSlice(SEQUENCE_LENGTH + payloadLength)));
                log.debug("resp_packet: \n{}", JSONUtil.parse(packet).toJSONString(2));
            } else {
                //todo 异常
            }
        }

        System.out.println("========== End of Proxy Client Received ==========");
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