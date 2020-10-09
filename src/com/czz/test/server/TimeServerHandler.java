package com.czz.test.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.UnsupportedEncodingException;

/**
 * @author : czz
 * @version : 1.0.0
 * @create : 2020-10-09 15:38:00
 * @description :
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 收到客户端消息，自动触发
     * @param ctx
     * @param msg
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        //将msg转为Netty的ByteBuf对象类似于JDK中的java.nio.ByteBuffer，不过 ButeBuf 功能更强，更灵活
        ByteBuf buf = (ByteBuf) msg;

        //buf.readableBytes()：获取缓冲区可读字节数，然后创建字节数组
        //从而避免了像 java.nio.ByteBuffer 时，只能盲目的创建特定大小的字节数组，比如 1024
        byte[] reg = new byte[buf.readableBytes()];

        //buf.readBytes()：将缓冲区字节数复制到新建的byte数组中
        //然后将字节数组转为字符串
        buf.readBytes(reg);
        String body = new String(reg,"UTF-8");
        System.out.println(Thread.currentThread().getName()+",The server receive  order : " + body);

        //回复消息
        //Unpooled.copiedBuffe：创建一个新的缓冲区，内容为里面的参数
        //通过ChannelHandlerContext的write方法将消息异步发送给客户端
        String respMsg = "我是Server，消息接收成功！";
        ByteBuf respByteBuf = Unpooled.copiedBuffer(respMsg.getBytes());
        ctx.write(respByteBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //flush：将消息发送队列中的消息写入到SocketChannel中发送给对方，为了频繁的唤醒Selector进行消息发送
        //Netty的write方法并不直接将消息写入SocketChannel中，调用write只是把待发送的消息放到缓存数组中，再通过调用flush方法
        //将发送缓冲区的消息全部写入到SocketChannel中
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //当发生异常，关闭ChannelHandlerContext，释放和它相关联的句柄等资源
        ctx.close();
    }
}
