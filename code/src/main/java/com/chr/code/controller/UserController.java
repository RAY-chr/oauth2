package com.chr.code.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sound.midi.Soundbank;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author RAY
 * @descriptions
 * @since 2020/1/28
 */
@Controller
public class UserController {

    private static final String client_id = "FxaK03LjOp";
    private static final String client_secret = "95130976-4da3-435e-a88c-8a142548a34c";
    private static final String redirect_uri = "http://127.0.0.1:8083/callback";
    /**
     * 进入获取用户名界面
     * @return
     */
    @RequestMapping("/user")
    public String user(){
        return "hello";
    }

    @RequestMapping("/")
    public String user2(){
        return "hello2";
    }



    @RequestMapping("/callback")
    @ResponseBody
    public String callback(HttpServletRequest request, HttpServletResponse response, HttpSession session)throws Exception{
        String code = request.getParameter("code");
        System.out.println("授权码"+code);
        //获取令牌
        String accessTokenParam = HttpRequestUtil.getAccessTokenParam(client_id, client_secret, redirect_uri, code);
        String result = HttpRequestUtil.getResult("http://47.96.187.200/profile/oauth2/accessToken", accessTokenParam);
        System.out.println("Token：   "+result);
        String token = getToken(result);
        System.out.println(token);
        //获取用户信息
        String userParam = HttpRequestUtil.getUserParam(client_id, client_secret, token);
        String user = HttpRequestUtil.getResult("http://47.96.187.200/profile/oauth2/profile", userParam);
        System.out.println("User:     "+user);
        String user1 = getUser(user);
        if (user1!=null) {
            session.setAttribute("user1", user1);
            response.sendRedirect("http://127.0.0.1:8083/");
        }
        System.out.println(user1);
        return "try";
    }
    public static void main(String[] args) {
        String s = "{\"status\":200,\"msg\":\"SUCCESS\",\"access_token\":\"PAT-792-iUemsH9iXykCUlNMmuSMPH1MwixM5K3VvAN7ygmbKZTEEV6yYD\"}" ;

        String s2="{\"status\":200,\"id\":\"testRay\",\"msg\":\"SUCCESS\",\"attributes\":[{\"language\":[\"zh\"]}]}";
        System.out.println(getUser(s2));

        System.out.println(getToken(s));

        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Boolean> future = service.submit(() -> {
            System.out.println(Thread.currentThread().getName());
            return true;
        });
        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static String getToken(String s){
        int index = s.lastIndexOf(":");
        String substring = s.substring(index + 1);
        int index1 = substring.lastIndexOf("}");
        String s1 = substring.substring(1, index1 - 1);
        return s1;
    }

    public static String getUser(String s){
        String substring = s.substring(20, 27);
        return substring;
    }
}
