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
    @Value("${mqconfig.stock_release_delay_queue}")
    private String stockReleaseDelayQueue;
    @Value("${mqconfig.stock_release_queue}")
    private String stockReleaseQueue;
    @Value("${mqconfig.stock_event_exchange}")
    private String stockEventExchange;
    @Value("${mqconfig.stock_release_delay_routing_key}")
    private String stockReleaseDelayRoutingKey;
    @Value("${mqconfig.stock_release_routing_key}")
    private String stockReleaseRoutingKey;
    @Value("${mqconfig.ttl}")
    private Integer ttl;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange stockEventExchange(){
        return new TopicExchange(stockEventExchange,true,false);
    }

    /**
     * Delay queue
     * @return
     */
    @Bean
    public Queue stockReleaseDelayQueue(){
        Map<String,Object> args = new HashMap<>();
        args.put(ConstantOnlineClass.RABBITMQ_X_DEAD_LETTER_ROUTING_KEY,stockReleaseRoutingKey);
        args.put(ConstantOnlineClass.RABBITMQ_X_DEAD_LETTER_EXCHANGE,stockEventExchange);
        args.put(ConstantOnlineClass.RABBITMQ_X_MESSAGE_TTL,ttl);
        return new Queue(stockReleaseDelayQueue,true,false,false,args);
    }

    /**
     * Dead letter queue
     */
    @Bean
    public Queue stockReleaseQueue(){
        return new Queue(stockReleaseQueue,true,false,false);

    }

    @Bean
    public Binding stockReleaseBinding(){
        return new Binding(stockReleaseQueue,Binding.DestinationType.QUEUE,stockEventExchange,stockReleaseRoutingKey,null);
    }

    @Bean
    public Binding stockReleaseDelayBinding(){
        return new Binding(stockReleaseDelayQueue,Binding.DestinationType.QUEUE,stockEventExchange,stockReleaseDelayRoutingKey,null);
    }
}
