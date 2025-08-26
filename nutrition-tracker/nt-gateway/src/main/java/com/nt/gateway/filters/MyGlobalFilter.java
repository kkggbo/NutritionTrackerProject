package com.nt.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.common.Result;
import com.nt.common.utils.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("拦截器拦截成功, path = " + path);

        // 登录和注册接口直接放行
        if (path.contains("login") || path.contains("register")) {
            return chain.filter(exchange);
        }

        // 获取 Authorization header
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token = headers.getFirst("Authorization");
        System.out.println("token = " + token);

        if (token != null && !token.isEmpty()) {
            try {
                // 解析 token
                Map<String, Object> claims = JwtUtils.parseJwt(token);
                Long userId = ((Number) claims.get("id")).longValue();

                System.out.println("token解析成功, userId = " + userId);

                // 把 userId 放到请求头中传递给后续服务
                exchange = exchange.mutate()
                        .request(r -> r.headers(h -> h.set("userId", String.valueOf(userId))))
                        .build();

                return chain.filter(exchange);

            } catch (Exception e) {
                return writeErrorResponse(exchange.getResponse(), 401, "请重新登录");
            }
        }

        // 没有 token
        return writeErrorResponse(exchange.getResponse(), 401, "请登录");
    }

    private Mono<Void> writeErrorResponse(ServerHttpResponse response, int code, String msg) {
        try {
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            Result<Object> result = Result.error(msg);
            result.setCode(code);

            ObjectMapper mapper = new ObjectMapper();
            String body = mapper.writeValueAsString(result);

            return response.writeWith(
                    Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8)))
            );
        } catch (Exception e) {
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1; // 优先级，越小越靠前
    }
}
