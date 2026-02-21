package com.rladntjd85.backoffice.common.config;

import com.rladntjd85.backoffice.auth.web.MustChangePasswordInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

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

    @Value("${app.upload.root-dir}")
    private String uploadRootDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String abs = Paths.get(uploadRootDir).toAbsolutePath().normalize().toString();
        String location = "file:" + (abs.endsWith("/") ? abs : abs + "/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
