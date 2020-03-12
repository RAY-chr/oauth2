package com.chr.code.controller;

import com.chr.code.lock.Mylock;
import com.chr.code.utils.RedisUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author RAY
 * @descriptions
 * @since 2020/3/10
 */
@RestController
public class RedisController {

    @Autowired
    private Redisson redisson;

    @Autowired
    private RedisUtils redisUtils;

    Mylock lock = new Mylock();

    /**
     *
     * @return
     */
    @RequestMapping("/decrease")
    public String decrease() {
        String redisKey = "delete";
        RLock lock = redisson.getLock(redisKey);
        try {
            lock.lock();
            String test = redisUtils.get("test");
            Integer value = Integer.valueOf(test);
            if (value > 0) {
                System.out.println("库存剩余： " + --value);
                redisUtils.set("test", String.valueOf(value));
                return "success";
            } else {
                System.out.println("没有库存");
                return "fail";
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 测试mylock
     * @return
     */
    @RequestMapping("/testMylock")
    public String testMyLock() {
        try {
            lock.lock();
            String test = redisUtils.get("test");
            Integer value = Integer.valueOf(test);
            if (value > 0) {
                System.out.println("库存剩余： " + --value);
                redisUtils.set("test", String.valueOf(value));
                return "success";
            } else {
                System.out.println("没有库存");
                return "fail";
            }
        } finally {
            lock.unlock();
        }
    }

}
