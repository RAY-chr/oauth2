package com.chr.mybatisplus.config;

import com.chr.mybatisplus.controller.HttpRequestUtil;
import com.chr.mybatisplus.utils.CookieUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

//使用WebMvcConfigurerAdapter可以来扩展SpringMVC的功能
//@EnableWebMvc   不要接管SpringMVC
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    private static final String APP_KEY = "56eea6c8e76fc4262a4a2816dfd79c7fd";

    private static final String clientId = "mybatisplus";

    private static final String client_secret = "02c781c9-a1a7-4cfe-8ba1-0ae0e6401116";

    private static String code = "0";


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/main.html").setViewName("dashboard");
    }


    /**
     * 注册自定义拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InterceptorDemo()).addPathPatterns("/show/list", "/borrowed","/test2", "/confirm", "/confirm2");
    }


    public class InterceptorDemo extends HandlerInterceptorAdapter {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
            //请求地址
            System.out.println("客户端请求的地址" + request.getRemoteAddr());
            code = request.getRemoteAddr();
            code = getSign(getParamsMap(code, clientId, client_secret), client_secret + APP_KEY);
            System.out.println("节点名为：  "+code);
            String token = HttpRequestUtil.getResultByGET("http://127.0.0.1:8080/code/" + code);
            if (token.length()==0) {
                response.sendRedirect("http://192.168.31.213:8080/login?clientId="
                        + new String(Base64.encodeBase64(clientId.getBytes()))
                        + "&redirectUrl="
                        + request.getRequestURL());
                return false;
            }
            request.getSession().setAttribute("token",token);
            request.getSession().setAttribute("code",code);
            CookieUtils.setCookie(request, response, "TT_TOKEN_KEY", token);
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        }

        @Override
        public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        }

        /**
         * CXF链接获取授权码
         *
         * @return
         */
        public String getCode() {
            // 创建动态客户端
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
            Client client = dcf.createClient("http://127.0.0.1:8080/soap/user?wsdl");
            // 需要密码的情况需要加上用户名和密码
            // client.getOutInterceptors().add(new ClientLoginInterceptor(USER_NAME, PASS_WORD));
            Object[] objects = new Object[0];
            try {
                // invoke("方法名",参数1,参数2,参数3....);
                objects = client.invoke("getCode", clientId);
                System.out.println("返回数据:" + objects[0]);
                String code = String.valueOf(objects[0]);
                return code;
            } catch (java.lang.Exception e) {
                System.out.println("failed to get code");
                return null;
            }

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
        private  Map getParamsMap(String request,String client_id,String client_secret){
            Map<String,String> map = new HashMap<>();
            map.put("request",request);
            map.put("client_id",client_id);
            map.put("client_secret",client_secret);
            return map;
        }


    }

}
