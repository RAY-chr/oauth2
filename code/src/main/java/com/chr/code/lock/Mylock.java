package com.chr.code.lock;

import sun.misc.Unsafe;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * @author RAY
 * @descriptions 基于AQS的锁  AQS三大要素 1、自旋（一直尝试拿锁） 2、LockSupport（控制当前线程cpu资源）
 *                                       3、CAS 原子操作 比较和替换
 * @since 2020/3/12
 */
public class Mylock {
    /**
     * 没人加锁为0，有人加锁就加1
     */
    private volatile int state = 0;

    /**
     * 当前持有锁的线程
     */
    private Thread lockholder;

    /**
     * 未加锁成功的队列
     */
    private ConcurrentLinkedQueue<Thread> waiters = new ConcurrentLinkedQueue<>();

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Thread getLockholder() {
        return lockholder;
    }

    public void setLockholder(Thread lockholder) {
        this.lockholder = lockholder;
    }

    /**
     * 尝试加锁 第一次被加锁waiters.size()==0 后面都是从队列出一个，判断当前线程是否为出队的线程
     * @return
     */
    private boolean acquire() {
        Thread current = Thread.currentThread();
        int state = this.getState();
        if (state == 0) {
            if ((waiters.size() == 0 || current == waiters.peek()) && compareAndSwapState(0, 1)) {
                this.setLockholder(current);
                return true;
            }
        }
        return false;
    }

    /**
     * 第一次未加锁成功的线程都入队，然后自旋（拿到cpu资源就尝试加锁）
     * 因为是公平锁 要判断当前线程是否为出队的第一个
     */
    public void lock() {
        if (acquire()) {
            return;
        }
        Thread current = Thread.currentThread();
        waiters.add(current);
        for (; ; ) {
            if (acquire() && current == waiters.peek()) {
                waiters.poll();
                return; //拿到锁就执行业务代码
            }
            LockSupport.park(current); //加锁不成功就停止cpu资源
        }
    }

    /**
     * 释放锁 释放锁之后要出队，然后给第一个出队线程cpu资源去尝试加锁
     */
    public void unlock() {
        Thread current = Thread.currentThread();
        if (current != this.getLockholder()) {
            throw new RuntimeException("lockholder is not current thread");
        }
        if (compareAndSwapState(this.getState(), 0)) {
            Thread first = waiters.peek();
            if (first != null) {
                LockSupport.unpark(first);
            }
        }
    }

    /**
     * 原子操作
     *
     * @param except
     * @param update
     * @return
     */
    private boolean compareAndSwapState(int except, int update) {
        return unsafe.compareAndSwapInt(this, stateOffset, except, update);
    }

    private static final Unsafe unsafe = UnsafeInstance.getUnsafe();
    private static long stateOffset;

    static {
        try {
            stateOffset = unsafe.objectFieldOffset(Mylock.class.getDeclaredField("state"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
