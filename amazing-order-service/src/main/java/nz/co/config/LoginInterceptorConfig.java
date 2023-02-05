package nz.co.config;

import nz.co.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginInterceptorConfig implements WebMvcConfigurer {
    @Bean
    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/api/order/*/**")
                .excludePathPatterns("/api/order/product_order/*/list")
                .excludePathPatterns("/api/callback/*/**","/api/order/*/query_state");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
