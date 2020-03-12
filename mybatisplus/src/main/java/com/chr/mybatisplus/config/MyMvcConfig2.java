package com.chr.mybatisplus.config;

import com.chr.mybatisplus.utils.CookieUtils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//使用WebMvcConfigurerAdapter可以来扩展SpringMVC的功能
//@EnableWebMvc   不要接管SpringMVC
//@Configuration
public class MyMvcConfig2 implements WebMvcConfigurer {



    private static final String clientId = "mybatisplus";


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/main.html").setViewName("dashboard");
    }


    /**
     * 注册自定义拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InterceptorDemo()).addPathPatterns("/show/list", "/test2", "/confirm", "/confirm2");
    }


    public class InterceptorDemo extends HandlerInterceptorAdapter {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
            Object userinfo = request.getSession().getAttribute("userinfo");
            if (userinfo==null) {
                response.sendRedirect("http://192.168.31.213:8080/login?clientId="
                        + new String(Base64.encodeBase64(clientId.getBytes()))
                        + "&redirectUrl="
                        + "http://192.168.31.213:8081/callback");
                return false;
            }
            String token = (String)request.getSession().getAttribute("token");
            CookieUtils.setCookie(request, response, "TT_TOKEN_KEY", token);
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

        }

        @Override
        public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

        }




    }

}
