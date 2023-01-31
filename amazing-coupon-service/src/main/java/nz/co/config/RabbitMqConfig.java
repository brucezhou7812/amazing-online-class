package nz.co.config;

import com.rabbitmq.client.AMQP;
import lombok.Data;
import nz.co.constant.ConstantOnlineClass;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Data
public class RabbitMqConfig {
    @Value("${mqconfig.coupon_release_delay_queue}")
    private String couponReleaseDelayQueue;
    @Value("${mqconfig.coupon_release_queue}")
    private String couponReleaseQueue;
    @Value("${mqconfig.coupon_event_exchange}")
    private String couponEventExchange;
    @Value("${mqconfig.coupon_release_delay_routing_key}")
    private String couponReleaseDelayRoutingKey;
    @Value("${mqconfig.coupon_release_routing_key}")
    private String couponReleaseRoutingKey;
    @Value("${mqconfig.ttl}")
    private Integer ttl;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange couponEventExchange(){
        return new TopicExchange(couponEventExchange,true,false);
    }

    /**
     * Delay queue
     * @return
     */
    @Bean
    public Queue couponReleaseDelayQueue(){
        Map<String,Object> args = new HashMap<>();
        args.put(ConstantOnlineClass.RABBITMQ_X_DEAD_LETTER_ROUTING_KEY,couponReleaseRoutingKey);
        args.put(ConstantOnlineClass.RABBITMQ_X_DEAD_LETTER_EXCHANGE,couponEventExchange);
        args.put(ConstantOnlineClass.RABBITMQ_X_MESSAGE_TTL,ttl);
        return new Queue(couponReleaseDelayQueue,true,false,false,args);
    }

    /**
     * Dead letter queue
     */
    @Bean
    public Queue couponReleaseQueue(){
        return new Queue(couponReleaseQueue,true,false,false);

    }

    @Bean
    public Binding couponReleaseBinding(){
        return new Binding(couponReleaseQueue,Binding.DestinationType.QUEUE,couponEventExchange,couponReleaseRoutingKey,null);
    }

    @Bean
    public Binding couponReleaseDelayBinding(){
        return new Binding(couponReleaseDelayQueue,Binding.DestinationType.QUEUE,couponEventExchange,couponReleaseDelayRoutingKey,null);
    }
}
