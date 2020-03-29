package com.chr.mybatisplus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chr.mybatisplus.entity.Book;
import com.chr.mybatisplus.entity.RentLog;
import com.chr.mybatisplus.entity.Renter;
import com.chr.mybatisplus.service.IBookService;
import com.chr.mybatisplus.service.IRentLogService;
import com.chr.mybatisplus.service.IRenterService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;

/**
 * <p>
 * 书籍表 前端控制器
 * </p>
 *
 * @author RAY
 * @since 2020-03-06
 */
@Controller
//@RequestMapping("/book")
public class BookController {

    private static final String APP_KEY = "56eea6c8e76fc4262a4a2816dfd79c7fd";

    private static final String clientId = "mybatisplus";

    private static final String client_secret = "02c781c9-a1a7-4cfe-8ba1-0ae0e6401116";

    @Autowired
    private IBookService bookService;

    @Autowired
    private IRentLogService logService;

    @Autowired
    private IRenterService renterService;

    /**
     * 书籍列表
     * @param model
     * @return
     */
    @RequestMapping("book")
    public String bookList(Model model){
        List<Book> books = bookService.list();
        model.addAttribute("books",books);
        return "emp/booklist";
    }

    /**
     * 借书
     * @param bookId
     * @param session
     * @return
     */
    @RequestMapping("borrowed/{bookId}")
    public String borrow(@PathVariable Integer bookId , HttpSession session){
        //控制器中调用多个service的时候，最好把多个写到一个service里面，方便事务回滚
        bookService.borrow(bookId,session.getAttribute("user").toString());
        return "redirect:/book";
    }

    /**
     * 回调方法，拿到授权码获取令牌
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("callback")
    public String callback(HttpServletRequest request)throws Exception{
        //拿到返回的授权码
        String code = request.getParameter("code");
        System.out.println("授权码为："+code);
        String addr = request.getRemoteAddr();
        if (addr.equals("192.168.31.213")) {
            addr = "0:0:0:0:0:0:0:1";
        }
        System.out.println("请求地址为"+request.getRemoteAddr());
        String sign = getSign(getParamsMap(code, addr, clientId, client_secret),
                client_secret + APP_KEY);
        System.out.println("签名code为："+sign);
        String token = HttpRequestUtil.getResultByGET("http://127.0.0.1:8080/code/" + sign);
        request.getSession().setAttribute("token",token);
        String result = HttpRequestUtil.getResultByGET("http://127.0.0.1:8080/token/" + token);
        System.out.println("用户信息为："+result);
        request.getSession().setAttribute("userinfo",result);
        request.getSession().setMaxInactiveInterval(30);
        return "redirect:/show/list";
    }


    /**
     * 签名算法
     * @param params
     * @param secret
     * @return
     */
    public  String getSign(Map<String, String> params, String secret) {
        String sign = "";
        StringBuilder sb = new StringBuilder();
        //排序
        Set<String> keyset = params.keySet();
        TreeSet<String> sortSet = new TreeSet<String>();
        sortSet.addAll(keyset);
        Iterator<String> it = sortSet.iterator();
        //加密字符串
        while (it.hasNext()) {
            String key = it.next();
            String value = params.get(key);
            sb.append(key).append(value);
        }
        sb.append("appkey").append(secret);
        try {
            sign = DigestUtils.md5Hex(sb.toString()).toUpperCase();
            //sign=  MD5Util.md5s(sb.toString()).toUpperCase();
        } catch (Exception e) {
        }
        return sign;
    }

    /**
     * 组装参数
     * @param request
     * @param client_id
     * @param client_secret
     * @return
     */
    private  Map getParamsMap(String code,String request,String client_id,String client_secret){
        Map<String,String> map = new HashMap<>();
        map.put("code",code);
        map.put("request",request);
        map.put("client_id",client_id);
        map.put("client_secret",client_secret);
        return map;
    }


}
