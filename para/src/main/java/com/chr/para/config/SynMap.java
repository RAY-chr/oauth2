package com.chr.para.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author RAY
 * @descriptions 管理zookeeper的节点与令牌
 * @since 2020/3/5
 */
public class SynMap {

    public static final Lock LOCK = new ReentrantLock();

    private static final Map<String,String> MAP = new HashMap<>();

    public synchronized static void put(String code,String token){
        MAP.put(code,token);
    }

    public synchronized static String get(String code){
        return MAP.get(code);
    }

    public synchronized static void remove(String code){
        MAP.remove(code);
    }

    public synchronized static Set<String> keys(){
        return MAP.keySet();
    }

}
