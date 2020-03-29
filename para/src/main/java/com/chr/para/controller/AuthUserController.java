package com.chr.para.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chr.para.config.SynMap;
import com.chr.para.curator.CuratorApi;
import com.chr.para.entity.AuthClient;
import com.chr.para.entity.AuthUser;
import com.chr.para.entity.ZookeeperNode;
import com.chr.para.service.IAuthClientService;
import com.chr.para.service.IAuthUserService;
import com.chr.para.service.IZookeeperNodeService;
import com.chr.para.utils.JsonUtils;
import com.chr.para.utils.RedisUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author RAY
 * @since 2020-01-09
 */
@Controller
//@RequestMapping("/authUser")
public class AuthUserController {

    private static final String APP_KEY = "56eea6c8e76fc4262a4a2816dfd79c7fd";

    @Autowired
    private IAuthUserService userService;

    @Autowired
    private IZookeeperNodeService nodeService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private IAuthClientService authClientService;


    /**
     * 验证登陆，存令牌于zookeeper，存信息于redis，回调到客户端的最开始请求地址
     *
     * @param request
     * @param username
     * @param password
     * @param session
     * @return
     */
    @PostMapping("user/login")
    public String login(HttpServletRequest request, String username, String password, HttpSession session) {
        System.out.println("客户端请求的地址" + request.getRemoteAddr());
        String clientId = String.valueOf(session.getAttribute("clientId"));
        String hex = DigestUtils.shaHex(password);
        AuthUser rest = userService.getLoginRest(username);
        if (rest == null) {
            session.setAttribute("msg", "用户名不存在");
            return "redirect:/login";
        } else if (rest.getLoginRest() == 0) {
            session.setAttribute("msg", "你的账户已被锁定");
            return "redirect:/login";
        } else if (username.equals("sysadmin")) {
            AuthUser one = userService.getOne(new QueryWrapper<AuthUser>()
                    .eq("username", username)
                    .eq("password", hex));
            if (one != null) {
                request.getSession().setAttribute("userinfo", one);
                return "redirect:/client";
            }
            return "redirect:/login";
        }
        AuthUser authUser = userService.getOne(new QueryWrapper<AuthUser>()
                .eq("username", username)
                .eq("password", hex)
                .eq("client_id", clientId));
        if (authUser != null) {
            String redirectUrl = String.valueOf(session.getAttribute("redirectUrl"));
            AuthClient client = authClientService.getOne(new QueryWrapper<AuthClient>().eq("client_id", clientId));
            String client_secret = client.getClientSecret();
            String code = request.getRemoteAddr();
            //为了使同一个局域网能访问，将请求地址变为了本机连接的wifi IP地址
            //0:0:0:0:0:0:0:1是本机localhost访问的ip地址，是ipv6的
            if (code.equals("192.168.31.213")) {
                code = "0:0:0:0:0:0:0:1";
            }
            /*String truecode = UUID.randomUUID().toString();
            System.out.println("授权码为："+truecode);*/
            code = getSign(getParamsMap(/*truecode,*/code, clientId, client_secret), client_secret + APP_KEY);
            System.out.println("签名code为："+code);
            String token = UUID.randomUUID().toString();
            nodeService.save(new ZookeeperNode(code, clientId, redirectUrl, authUser.getId()));
            userService.storeToken(code, token);
            try {
                SynMap.LOCK.lock();
                SynMap.put(code,token);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SynMap.LOCK.unlock();
            }
            authUser.setPassword("0");
            redisUtils.set("UserToken:" + token, JsonUtils.objectToJson(authUser));
            redisUtils.expire("UserToken:" + token, 3600, TimeUnit.SECONDS);
            return "redirect:" + session.getAttribute("redirectUrl")/*+"?code="+truecode*/;
        }
        session.setAttribute("msg", "输入密码不对,你还有" + (rest.getLoginRest() - 1) + "次机会");
        userService.updateLoginRest(new AuthUser(rest.getId(), rest.getLoginRest() - 1));
        /**
         * 只更新少量的字段用UpdateWrapper
         * 下面代码对应的sql  UPDATE auth_user SET login_rest=? WHERE (client_id = ?)
         */
        /*userService.update(null,new UpdateWrapper<AuthUser>()
                .eq("client_id","qwertgf")
                .set("login_rest",9));*/
        return "redirect:/login";

    }

    /**
     * 根据节点获取令牌
     * @param code
     * @return
     */
    @RequestMapping("/code/{code}")
    @ResponseBody
    public String getByCode(@PathVariable String code){
        try {
            String token = CuratorApi.INSTANCE.getTokenByNode(code);
            if (token==null){
                return null;
            }
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/deleteCode/{code}")
    @ResponseBody
    public String deleteCode(@PathVariable String code)throws Exception{
        CuratorApi.INSTANCE.deleteBatchNoNode(code);
        return "success";
    }

    /**
     * 校验令牌
     *
     * @param token
     * @return
     */
    @RequestMapping("/token/{token}")
    @ResponseBody
    public AuthUser getByToken(@PathVariable String token) {
        AuthUser user = userService.getByToken(token, AuthUser.class);
        if (user == null) {
            return null;
        }
        return user;
    }

    /**
     * 签名算法
     *
     * @param params
     * @param secret
     * @return
     */
    public static String getSign(Map<String, String> params, String secret) {
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
        } catch (Exception e) {
        }
        return sign;
    }

    /**
     * 封装签名算法的参数
     *
     * @param request
     * @param client_id
     * @param client_secret
     * @return
     */
    private static Map getParamsMap(/*String code,*/String request, String client_id, String client_secret) {
        Map<String, String> map = new HashMap<>();
       /* map.put("code",code);*/
        map.put("request", request);
        map.put("client_id", client_id);
        map.put("client_secret", client_secret);
        return map;
    }

    /**
     * 用户列表
     *
     * @param model
     * @return
     */
    @RequestMapping("/user/{clientId}")
    public String getUserList(Model model, @PathVariable String clientId) {
        if (clientId != null && !clientId.equals("all")) {
            List<AuthUser> client_id = userService.list(new QueryWrapper<AuthUser>()
                    .eq("client_id", clientId));
            List<AuthUser> collect = client_id.stream()
                    .map(this::getCache)
                    .collect(Collectors.toList());
            model.addAttribute("users", collect);
            return "emp/userlist";
        }
        List<AuthUser> users = userService.list();
        List<AuthUser> authUsers = users.stream()
                .filter(x -> !x.getUsername().equals("sysadmin"))
                .map(this::getCache)
                .collect(Collectors.toList());
        model.addAttribute("users", authUsers);
        return "emp/userlist";
    }

    /**
     * 跳转到用户添加页面
     *
     * @param model
     * @return
     */
    @RequestMapping("userAdd")
    public String addUser(Model model) {
        List<AuthClient> clients = authClientService.list();
        model.addAttribute("clientIds", clients);
        return "emp/useradd";
    }

    /**
     * 添加用户
     *
     * @param authUser
     * @return
     */
    @PostMapping("addUser")
    public String add(AuthUser authUser) {
        authUser.setPassword(DigestUtils.shaHex(authUser.getPassword()));
        authUser.setLoginRest(10);
        userService.save(authUser);
        return "redirect:/user/all";
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @RequestMapping("authuser/{id}")
    public String delete(@PathVariable Integer id) {
        userService.removeById(id);
        return "redirect:/user/all";
    }

    /**
     * 根据用户名查找用户
     * @param username
     * @param model
     * @return
     */
    @PostMapping("search")
    public String search(String username,Model model){
        if (username.length()==0){
            return "redirect:/user/all";
        }
        List<AuthUser> list = userService.list(new QueryWrapper<AuthUser>()
                .like("username", username)
                .ne("username","sysadmin"));
        List<AuthUser> collect = getData(list, /*x -> {
            x.setClientId(redisUtils.get("CLIENT:"+x.getClientId()));
            return x;
        }*/this::getCache);
        model.addAttribute("users",collect);
        model.addAttribute("user",username);
        return "emp/userlist";
    }

    /**
     * 公共的代码可以抽出来
     * @param x
     * @return
     */
    public AuthUser getCache(AuthUser x){
        x.setClientId(redisUtils.get("CLIENT:"+x.getClientId()));
        return x;
    }


    /**
     * 返回自定义集合
     * @param list
     * @param fun
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> List<R> getData(List<T> list, Function<T,R> fun){
        List<R> collect = list.stream().map(t -> fun.apply(t)).collect(Collectors.toList());
        return collect;
    }



    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("request", "0:0:0:0:0:0:0:1");
        map.put("client_id", "mybatisplus");
        map.put("client_secret", "02c781c9-a1a7-4cfe-8ba1-0ae0e6401116");
        String sign = getSign(map, "02c781c9-a1a7-4cfe-8ba1-0ae0e6401116" + APP_KEY);
        System.out.println(sign);
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        List<String> data = getData(list, x -> String.valueOf(x) + "fun");
        System.out.println(data);

        SynMap.put("xxx","111");
        SynMap.remove("xxx");
        SynMap.put("xxx","222");
        System.out.println(SynMap.get("xxx"));
    }

}
