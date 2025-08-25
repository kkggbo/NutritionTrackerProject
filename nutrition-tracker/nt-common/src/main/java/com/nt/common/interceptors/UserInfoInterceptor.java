package com.nt.common.interceptors;

import com.nt.common.utils.UserThreadLocal;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserInfoInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取登录用户信息
        String userId = request.getHeader("userId");

        // 判断是否获取到用户信息，如果有，存入ThreadLocal
        if (StringUtils.isNotBlank(userId)){
            UserThreadLocal.setUserId(Long.valueOf(userId));
        }

        // 放行
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        // 清除ThreadLocal
        UserThreadLocal.remove();
    }
}
