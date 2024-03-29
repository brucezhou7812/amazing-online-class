package nz.co.config;

import nz.co.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sun.rmi.runtime.Log;
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Bean
    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/api/coupon/*/**")
                .addPathPatterns("/api/couponrecord/*/**")
                .excludePathPatterns("/api/coupon/*/list_coupon_inpage")
                .excludePathPatterns("/api/coupon/*/add_new_user_coupon");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
