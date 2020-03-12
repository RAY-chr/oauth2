package com.chr.para.config;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author RAY
 * @descriptions  保存了客户端系统新的主动推送批次号
 * @since 2020/2/28
 */
public class SynSet {

    public static final Lock LOCK = new ReentrantLock();

    private static final Set<String> batchNoSet = new HashSet<>();

    /**
     * 加锁保证唯一性
     * @param s
     */
    public static synchronized void add(String s){
        batchNoSet.add(s);
    }

    public static synchronized Set<String> get(){
        return batchNoSet;
    }

    public static synchronized void clear(){
        batchNoSet.clear();
    }
}
