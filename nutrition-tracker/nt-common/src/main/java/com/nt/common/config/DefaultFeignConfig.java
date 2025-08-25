package com.nt.common.config;

import com.nt.common.utils.UserThreadLocal;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return new RequestInterceptor(){
            @Override
            public void apply(feign.RequestTemplate template) {
                Long userId = UserThreadLocal.getUserId();
                if(userId != null){
                    template.header("userId", userId.toString());
                }
            }
        };
    }
}
