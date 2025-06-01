package com.nt.tracker.utils;

public class UserThreadLocal {
    private static final ThreadLocal<Long> userThreadLocal = new ThreadLocal<>();

    // 设置用户ID
    public static void setUserId(Long userId) {
        userThreadLocal.set(userId);
    }

    // 获取用户ID
    public static Long getUserId() {
        return userThreadLocal.get();
    }

    // 清除ThreadLocal，防止内存泄漏
    public static void remove() {
        userThreadLocal.remove();
    }
}
