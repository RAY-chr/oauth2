package com.chr.mybatisplus.netty;

import com.chr.mybatisplus.entity.Book;
import com.chr.mybatisplus.service.IBookService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author RAY
 * @descriptions
 * @since 2020/3/29
 */
@Component
@Scope("prototype")
public class HttpServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    private IBookService bookService;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        //【因为@Autowired 必须主类被spring管理才能使用，这样的话HttpServer也要加入spring容器
        // 而且需保证HttpServer是单例的，所以手动获取bean】
       // IBookService bookService = (IBookService)SpringUtil.getBean("bookServiceImpl");
        List<Book> books = bookService.list();
        for (Book book : books) {
            System.out.println(book);
        }
        String text = frame.text();
        ctx.channel().writeAndFlush(new TextWebSocketFrame("[服务器]返回消息：  "+text));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("链接客户端。。。");
    }
}
