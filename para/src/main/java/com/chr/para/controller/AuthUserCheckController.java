package com.chr.para.controller;


import com.chr.para.config.SynSet;
import com.chr.para.entity.AuthUser;
import com.chr.para.entity.AuthUserCheck;
import com.chr.para.entity.UserList;
import com.chr.para.service.IAuthUserCheckService;
import com.chr.para.service.IAuthUserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author RAY
 * @since 2020-02-28
 */
@Controller
//@RequestMapping("/authUserCheck")
public class AuthUserCheckController {

    private ExecutorService service = Executors.newSingleThreadExecutor();

    private static Logger logger = LoggerFactory.getLogger(AuthUserCheckController.class);

    @Autowired
    private IAuthUserCheckService userCheckService;

    @Autowired
    private IAuthUserService userService;

    /**
     * 接收客户端系统的用户数据
     * @param list
     * @return
     */
    @RequestMapping("/userList")
    @ResponseBody
    public String insertUserList(@RequestBody UserList list) {
        List<AuthUserCheck> checks = list.getAuthUsers().stream()
                .map(x -> {
                    x.setPassword(DigestUtils.shaHex(x.getPassword()));
                    x.setBatchNo(list.getBatchNo());
                    return x;
                })
                .collect(Collectors.toList());
        // 防止从check表到用户表同步时间太长，接口返回成功状态太慢
        // 所以直接开线程(用线程池)处理添加新的批次号，因为新增批次号不会过于频繁
        service.submit(() -> {
            try {
                SynSet.LOCK.lock();
                SynSet.add(list.getBatchNo());
                logger.info("批次号 {} 添加至同步Set集合中",list.getBatchNo());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SynSet.LOCK.unlock();
            }
        });
        logger.info("批次号 {} 添加成功", list.getBatchNo());
        boolean batch = userCheckService.saveBatch(checks);
        return batch == true ? "success" : "failed";
    }

    /**
     * 测试直接传递list参数  postman的body必须是最外层是[],里面是一个个的{}
     * SpringMvc接受集合类型的参数时候，必须加 @RequestBody
     * @param list
     * @return
     */
    @RequestMapping("/test")
    @ResponseBody
    public String test(@RequestBody List<AuthUser> list){
        list.forEach(System.out::println);
        return "success";
    }




}
