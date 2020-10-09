package com.czz.test.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author : czz
 * @version : 1.0.0
 * @create : 2020-10-09 16:03:00
 * @description :
 */
public class TimeClient {
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new Thread(new MyThread()).start();
        }
    }
    static class MyThread implements Runnable{
        @Override
        public void run() {
            connect("127.0.0.1", 9898);
        }
        public void connect(String host, int port){
            //配置客户端NIO线程池
            EventLoopGroup group = new NioEventLoopGroup();

            try{
                //Bootstrap与ServerBootstrap都继承(extends)于 AbstractBootstrap
                //创建客户端辅助启动类,并对其配置,与服务器稍微不同，这里的 Channel 设置为 NioSocketChannel
                //然后为其添加 Handler，这里直接使用匿名内部类，实现 initChannel 方法
                //作用是当创建 NioSocketChannel 成功后，在进行初始化时,将它的ChannelHandler设置到ChannelPipeline中，用于处理网络I/O事件
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group).channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline().addLast(new TimeClientHandler());
                            }
                        });
                //connect发起异步连接操作，调用同步方法sync等待连接成功
                ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
                System.out.println(Thread.currentThread().getName() + ",客户端发起异步连接.....");

                //等待客户端链路关闭
                channelFuture.channel().closeFuture().sync();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                //退出
                group.shutdownGracefully();
            }
        }
    }
}
