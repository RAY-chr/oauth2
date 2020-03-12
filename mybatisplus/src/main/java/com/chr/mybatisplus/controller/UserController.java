package com.chr.mybatisplus.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chr.mybatisplus.entity.AuthUser;
import com.chr.mybatisplus.entity.User;
import com.chr.mybatisplus.service.IUserService;
import com.chr.mybatisplus.utils.CookieUtils;
import com.chr.mybatisplus.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author RAY
 * @since 2019-12-06
 */
@Controller
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 分页实际效果
     *
     * @return
     */
    @RequestMapping("/show/list")
    public String showList() {
        return "list";
    }

    /**
     * 分页查询
     *
     * @param current
     * @param size
     * @param age
     * @param sex
     * @return
     */
    @RequestMapping(value = "/show/{current}/{size}", method = RequestMethod.POST)
    @ResponseBody
    public Page<User> getByPage(@PathVariable long current, @PathVariable long size,
                                @RequestParam(defaultValue = "0") int age,
                                @RequestParam(defaultValue = "1") int sex) {
        //3.0版本条件构造器改为QueryWrapper了
        Page<User> page = userService.page(new Page<User>(current, size), new QueryWrapper<User>()
                .gt("age", age)
                .eq("sex", sex));
        //System.out.println(page.getTotal());
        return page;
    }



    @RequestMapping("/index")
    public String tologin() {
        return "login";
    }



    @GetMapping("test")
    @ResponseBody
    public String login() throws Exception{
        MultiThreadDown.transfer();
        return "true";
    }

    @GetMapping("down")
    @ResponseBody
    public String login3(HttpServletRequest request,HttpServletResponse response) throws Exception{
        DownUtils.downFile(request,response,"E:\\UbuntuIso\\ubuntu-18.04.2-desktop-amd64.iso");
        return "true";
    }

    @GetMapping("test2")
    public String login2(Model model, HttpServletRequest request)throws Exception {
        Object token = request.getSession().getAttribute("token");
        System.out.println("令牌为"+token);
        String result = HttpRequestUtil.getResultByGET("http://127.0.0.1:8080/token/" + token);
        System.out.println("结果为："+result);
        AuthUser user = JsonUtils.jsonToPojo(result, AuthUser.class);
        model.addAttribute("user",user.getUsername());
        request.getSession().setAttribute("user",user.getUsername());
        return "redirect:/borrowed";
    }

    /**
     * 因为不能直接返回重定向回来的地址，所以需要有一个中间页面跳转
     *
     * @param redirectUrl
     * @return
     */
    @RequestMapping("toConfirm")
    public String toConfirm(HttpServletRequest request, HttpServletResponse response, String redirectUrl) {
        //把回掉的地址存入cookie以便重定向回去，如果直接请求confirm接口，第三方授权服务器回调时没有redirectUrl参数
        CookieUtils.setCookie(request, response, "redirectUrl", redirectUrl);
        return "toConfirmPage";
    }

    /**
     * 被拦截到第三方登录并且返回原来的地址
     *
     * @param redirectUrl
     * @return
     */
    @RequestMapping("confirm")
    public String confirm() {
        return "toPrimitivePage";
    }

    /**
     * 测试直接被拦截器拦截的地址返回重定向地址会报错
     *
     * @param redirectUrl
     * @return
     */
    @RequestMapping("confirm2")
    public String confirm2(String redirectUrl) {
        return "redirect:" + redirectUrl;
    }

    /**
     * 单点退出
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("loginOut")
    public String loginOut(HttpServletRequest request,HttpServletResponse response)throws Exception{
        String code = (String)request.getSession().getAttribute("code");
        System.out.println("节点为："+code);
        HttpRequestUtil.getResultByGET("http://127.0.0.1:8080/deleteCode/" + code);
        CookieUtils.deleteCookie(request,response,"TT_TOKEN_KEY");
        return "redirect:/test2";
    }


}
