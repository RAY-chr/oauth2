package com.chr.mybatisplus.config;

import com.chr.mybatisplus.netty.HttpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author RAY
 * @descriptions
 * @since 2020/3/29
 */
@Component
public class NettyBooter implements ApplicationRunner {
    @Autowired
    private HttpServer httpServer;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //尽管HttpServer加入了spring容器，但是new出来的HttpServer实例并没有初始化他的Autowired下的成员
        //所以如果用new的方式，尽管注入了HttpServerHandler，但是值却是空的
       // HttpServer.getInstance().start();
        httpServer.start();
    }
}
