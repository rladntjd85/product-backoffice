package com.rladntjd85.backoffice.common.config;

import com.rladntjd85.backoffice.auth.web.MustChangePasswordInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MustChangePasswordInterceptor mustChangePasswordInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mustChangePasswordInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/**"); // API(JWT)는 제외
    }
}
