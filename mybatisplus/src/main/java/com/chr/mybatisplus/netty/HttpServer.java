package com.chr.mybatisplus.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author RAY
 * @descriptions Netty的Handler必须是多例的
 * @since 2020/3/29
 */
@Component
public class HttpServer {
    /*@Autowired
    private HttpServerHandler serverHandler;*/

    /**   Autowired默认单例，多例使用需如下
     * 1、在需要多例调用的类上加@Scope("prototype")

       2、在进行注入时，不能直接使用@Autowired，否则注入的还是单例，需要使用工厂，最简单的是用
          @Autowired
          private ObjectFactory<T> objectFactory;
          对象进行注入（T为你要注入的类），想要使用该多例对象时，用
          T t = objectFactory.getObject();
     */
    @Autowired
    private ObjectFactory<HttpServerHandler> objectFactory;

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private static class Inner{
        private static final HttpServer INSTANCE = new HttpServer();
    }
    public static HttpServer getInstance(){
        return Inner.INSTANCE;
    }

    public void start()throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup,workerGroup) //设置链接和工作线程组
                    .channel(NioServerSocketChannel.class)  //设置服务端
                    .option(ChannelOption.SO_BACKLOG,128) //设置客户端连接队列的链接个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true) //设置保持活动链接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            ChannelPipeline pipeline = sc.pipeline();
                            // websocket 基于http协议，所以要有http编解码器
                            pipeline.addLast(new HttpServerCodec());
                            // 对写大数据流的支持
                            pipeline.addLast(new ChunkedWriteHandler());
                            // 对httpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse
                            // 几乎在netty中的编程，都会使用到此hanler
                            pipeline.addLast(new HttpObjectAggregator(1024*64));
                            /*
                             * websocket 服务器处理的协议，用于指定给客户端连接访问的路由 : /ws
                             * 本handler会帮你处理一些繁重的复杂的事
                             * 会帮你处理握手动作： handshaking（close, ping, pong） ping + pong = 心跳
                             * 对于websocket来讲，都是以frames进行传输的，不同的数据类型对应的frames也不同
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                            System.out.println(objectFactory.getObject());
                            pipeline.addLast(objectFactory.getObject());
                        }
                    });  //给workerGroup的 EventLoop对应的管道设置处理器
            logger.info("服务器 is ready");
            //启动服务器绑定端口 用作http长连接的话，端口最好取80开头的，例如6667就不行
            ChannelFuture fu = bootstrap.bind(8088).sync();
            fu.addListener(future->{
                if (future.isSuccess()){
                    logger.info("监听端口成功");
                }else {
                    System.out.println("监听失败");
                }
            });
            //监听通道
            fu.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

        }
    }
}

