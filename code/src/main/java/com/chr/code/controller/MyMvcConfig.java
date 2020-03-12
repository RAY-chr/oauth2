package com.chr.code.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//使用WebMvcConfigurerAdapter可以来扩展SpringMVC的功能
//@EnableWebMvc   不要接管SpringMVC
//@Configuration
public class MyMvcConfig implements WebMvcConfigurer {


    private static final String clientId = "mybatisplus";

    private static String code = "0";


    /**
     * 注册自定义拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InterceptorDemo()).addPathPatterns("/**").excludePathPatterns("/callback","/user");
    }


    public class InterceptorDemo extends HandlerInterceptorAdapter {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {

            HttpSession session = request.getSession();
            Object user = session.getAttribute("user1");
            if (user==null){
                response.sendRedirect("http://47.96.187.200/profile/oauth2/authorize?client_id=FxaK03LjOp&redirect_uri=http://127.0.0" +
                        ".1:8083/callback&oauth_timestamp="+System.currentTimeMillis()+"response_type=code");
            }
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
            System.out.println("拦截器1 postHandle： ");
        }

        @Override
        public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
            System.out.println("拦截器1 afterCompletion： ");
        }


    }

}
