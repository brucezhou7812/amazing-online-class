package nz.co.config;

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
    @Value("${mqconfig.order_close_delay_queue}")
    private String orderCloseDelayQueue;
    @Value("${mqconfig.order_close_queue}")
    private String orderCloseQueue;
    @Value("${mqconfig.order_event_exchange}")
    private String orderEventExchange;
    @Value("${mqconfig.order_close_delay_routing_key}")
    private String orderCloseDelayRoutingKey;
    @Value("${mqconfig.order_close_routing_key}")
    private String orderCloseRoutingKey;
    @Value("${mqconfig.ttl}")
    private Integer ttl;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange(orderEventExchange,true,false);
    }

    /**
     * Delay queue
     * @return
     */
    @Bean
    public Queue orderCloseDelayQueue(){
        Map<String,Object> args = new HashMap<>();
        args.put(ConstantOnlineClass.RABBITMQ_X_DEAD_LETTER_ROUTING_KEY,orderCloseRoutingKey);
        args.put(ConstantOnlineClass.RABBITMQ_X_DEAD_LETTER_EXCHANGE,orderEventExchange);
        args.put(ConstantOnlineClass.RABBITMQ_X_MESSAGE_TTL,ttl);
        return new Queue(orderCloseDelayQueue,true,false,false,args);
    }

    /**
     * Dead letter queue
     */
    @Bean
    public Queue orderCloseQueue(){
        return new Queue(orderCloseQueue,true,false,false);

    }

    @Bean
    public Binding orderCloseBinding(){
        return new Binding(orderCloseQueue,Binding.DestinationType.QUEUE,orderEventExchange,orderCloseRoutingKey,null);
    }

    @Bean
    public Binding orderCloseDelayBinding(){
        return new Binding(orderCloseDelayQueue,Binding.DestinationType.QUEUE,orderEventExchange,orderCloseDelayRoutingKey,null);
    }
}
