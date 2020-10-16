package com.czz.test.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author : czz
 * @version : 1.0.0
 * @create : 2020-10-09 15:10:00
 * @description :
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 9898;
        new TimeServer().bind(port);
    }
    public void bind(int port){
        //bossGroup用于服务端接受客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //workGroup进行SocketChannel网络读写
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            //serverBootstrap是Netty用于启动NIO服务端辅助启动类，用于降低开发难度
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());

            //服务器启动辅助类配置完成后，调用bind方法绑定监听端口
            // 调用sync方法同步等待绑定操作完成
            ChannelFuture cf = sb.bind(port).sync();

            System.out.println(Thread.currentThread().getName()+"，服务器开始监听端口，等待客户端连接");

            //下面方法进行阻塞，等待服务器连接关闭之后main方法退出，程序结束
            cf.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel socketChannel) {
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }
}
