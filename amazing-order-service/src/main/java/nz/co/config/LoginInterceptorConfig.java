package nz.co.config;

import feign.RequestInterceptor;
import nz.co.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

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
                //.excludePathPatterns("/api/order/product_order/*/list")
                .excludePathPatterns("/api/callback/*/**","/api/order/*/query_state","/api/order/product_order/v1/testAlipay");
        WebMvcConfigurer.super.addInterceptors(registry);
    }


}
