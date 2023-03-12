package nz.co.config;

import feign.RequestInterceptor;
import nz.co.component.RabbitRequestAttributes;
import nz.co.constant.ConstantOnlineClass;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
@Configuration
public class RequestInterceptorConfig {
   @Bean
    public RequestInterceptor requestInterceptor(){
        return template -> {
            //ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if(attributes != null) {
                if(attributes instanceof ServletRequestAttributes) {
                    ServletRequestAttributes sra = (ServletRequestAttributes)attributes;
                    HttpServletRequest request = sra.getRequest();
                    if (request == null) return;
                    String token = request.getParameter("token");
                    template.header("token", token);
                }else if(attributes instanceof RabbitRequestAttributes){
                    RabbitRequestAttributes rra = (RabbitRequestAttributes)attributes;
                    String token =(String) rra.getAttribute(ConstantOnlineClass.TOKEN,0);
                    if (StringUtils.isBlank(token)) return;
                    template.header("token", token);
                }
            }
        };
    }
}
