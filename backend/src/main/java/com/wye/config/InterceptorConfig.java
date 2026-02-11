package com.wye.config;

import com.wye.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
   
            @Autowired
    private AuthInterceptor authInterceptor;
   
            @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                                        "/user/login",
                                        "/user/register/**",
                                        "/common/**",
                                        "/notice/list",
                                        "/activity/list",
                                        "/carousel/list",
                                        "/error",
                                        "/upload/**",
                                        "/api/upload/**",
                                        "/forum/list",
                                "/forum/category/list",
                                "/forum/category/**"
                                );
    }
}

