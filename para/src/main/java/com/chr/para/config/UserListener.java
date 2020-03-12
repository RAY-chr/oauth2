package com.chr.para.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chr.para.curator.CuratorApi;
import com.chr.para.entity.AuthUser;
import com.chr.para.entity.AuthUserCheck;
import com.chr.para.service.IAuthUserCheckService;
import com.chr.para.service.IAuthUserService;
import com.chr.para.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author RAY
 * @descriptions 定时推送check表数据到用户表
 * @since 2020/2/28
 */
@Configuration
public class UserListener implements ServletContextListener {

    private static final List<String> TOKEN_LIST = new ArrayList<>();

    //保存了认证用户数据
    private static final List<AuthUser> LIST = new ArrayList<>();

    private static Logger logger = LoggerFactory.getLogger(UserListener.class);

    @Autowired
    private IAuthUserCheckService userCheckService;

    @Autowired
    private IAuthUserService userService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 1、加锁防止批次号Set被清空之前客户端系统新来批次号，这样的话会丢失新的批次号
     * 2、实时检测redis的过时令牌
     * @param servletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        executor.scheduleAtFixedRate(() -> {
            try {
                SynSet.LOCK.lock();
                if (SynSet.get().size() != 0) {
                    logger.info("当前批次号为 {} ", SynSet.get());
                    for (String s : SynSet.get()) {
                        List<AuthUserCheck> list =
                                userCheckService.list(new QueryWrapper<AuthUserCheck>().eq("batch_no", s));
                        logger.info("当前批次号上传的用户数据为 {} ", list);
                        AuthUser user = null;
                        for (AuthUserCheck check : list) {
                            user = new AuthUser();
                            user.setUsername(check.getUsername());
                            user.setPassword(check.getPassword());
                            user.setClientId(check.getClientId());
                            user.setQq(check.getQq());
                            user.setLoginRest(check.getLoginRest());
                            LIST.add(user);
                        }
                    }
                    userService.saveBatch(LIST);
                    /*System.out.println("睡一会");
                    Thread.sleep(10000);
                    System.out.println("睡好了");*/
                    logger.info(">>>>  清空当前批次号 {}  开始<<<<", SynSet.get());
                    SynSet.clear();
                    LIST.clear();
                    logger.info(">>>>  清空当前批次号完成  <<<<");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SynSet.LOCK.unlock();
            }
        }, 1000, 100*1000, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(()->{
            try {
                SynMap.LOCK.lock();
                logger.info("======实时检测redis的过时令牌中======");
                for (String code : SynMap.keys()){
                    String token = CuratorApi.INSTANCE.getTokenByNode(code);
                    if (token==null){
                        logger.info("======检测到redis中的过时令牌 {} 开始清除======","UserToken:"+SynMap.get(code));
                        redisUtils.delete("UserToken:"+SynMap.get(code));
                        logger.info("======删除过时令牌完成======");
                        TOKEN_LIST.add(code);
                    }
                }
                if (TOKEN_LIST.size()!=0) {
                    TOKEN_LIST.forEach(x -> SynMap.remove(x));
                    TOKEN_LIST.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SynMap.LOCK.unlock();
            }
        },1000,100*1000,TimeUnit.MILLISECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
