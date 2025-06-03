package com.nt.tracker.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.tracker.common.Result;
import com.nt.tracker.utils.JwtUtils;
import com.nt.tracker.utils.UserThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("拦截器拦截成功");
        // 获取当前请求的URL
        String url = request.getRequestURI();

        // 设置响应类型为JSON，防止中文乱码
        response.setContentType("application/json;charset=utf-8");

        // 如果是登录或注册请求，直接放行
        if (url.contains("login") || url.contains("register")) {
            return true;
        }

        // 获取当前用户的token
        String token = request.getHeader("Authorization");

        // 检查token是否为空，不为空则需校验是否有效
        if (token != null && !token.isEmpty()) {
            try {
                // 获取当前用户id
                Map<String, Object> claims = JwtUtils.parseJwt(token);
                Long userId = ((Number) claims.get("id")).longValue(); // 避免 ClassCastException

                // 将id存入Threadlocal
                UserThreadLocal.setUserId(userId);

                // 校验通过，放行
                return true;
            } catch (Exception e) {
                // 令牌解析失败，返回错误结果（令牌无效）
                // e.printStackTrace();
                Result<Object> result = Result.error("请重新登录");
                result.setCode(401);
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(result);
                response.getWriter().write(json);
                response.getWriter().flush();
                return false;
            }
        }

        // token不存在，返回错误结果（未登录）
        Result<Object> result = Result.error("请登录");
        // 将结果写入响应体并返回false
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(result);
        response.getWriter().write(json);
        response.getWriter().flush();
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        System.out.println("清除ThreadLocal");
        UserThreadLocal.remove();
    }


}
