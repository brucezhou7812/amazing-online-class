package nz.co.component;

import lombok.extern.slf4j.Slf4j;
import nz.co.constant.ConstantOnlineClass;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
@Slf4j
public class RabbitMQMessagePostProcessor implements MessagePostProcessor {
    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(sra == null){
           log.warn("postProcessMessage: can not get request attributes from context.");
           return null;
        }
        HttpServletRequest request = sra.getRequest();
        if(request == null){
            log.warn("postProcessMessage: can not get request from request attributes.");
            return null;
        }
        String token = getTokenFromRequest(request);
        if(StringUtils.isBlank(token)){
            log.warn("postProcessMessage: Token is empty.");
            return null;
        }
        message.getMessageProperties().setHeader(ConstantOnlineClass.TOKEN,token);
        return message;
    }

    private String getTokenFromRequest(HttpServletRequest request){
        String token = request.getHeader("token");
        if(StringUtils.isBlank(token)){
            token = request.getParameter("token");
        }
        return token;
    }
}
