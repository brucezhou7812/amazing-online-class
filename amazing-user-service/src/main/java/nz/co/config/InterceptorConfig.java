package nz.co.config;

import lombok.extern.slf4j.Slf4j;
import nz.co.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {

    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

       registry.addInterceptor(loginInterceptor())
       .addPathPatterns("/api/user/**","/api/notify/**","/api/address/**")
               .excludePathPatterns("/api/notify/*/send_code",
                       "/api/user/*/register",
                       "/api/user/*/login",
                       "/api/user/*/upload_user_img",
                       "/api/notify/*/kaptcha");

    }
}
