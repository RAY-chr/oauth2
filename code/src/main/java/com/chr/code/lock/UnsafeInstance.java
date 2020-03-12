package com.chr.code.lock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author RAY
 * @descriptions
 * @since 2020/3/12
 */
public class UnsafeInstance {
    private static  Unsafe unsafe;
    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public static Unsafe getUnsafe(){
        return unsafe;
    }
}
