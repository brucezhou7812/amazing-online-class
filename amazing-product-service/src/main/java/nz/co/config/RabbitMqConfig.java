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
    @Value("${mqconfig.cart_release_delay_queue}")
    private String cartReleaseDelayQueue;
    @Value("${mqconfig.cart_release_queue}")
    private String cartReleaseQueue;
    @Value("${mqconfig.cart_event_exchange}")
    private String cartEventExchange;
    @Value("${mqconfig.cart_release_delay_routing_key}")
    private String cartReleaseDelayRoutingKey;
    @Value("${mqconfig.cart_release_routing_key}")
    private String cartReleaseRoutingKey;
    @Value("${mqconfig.stock_ttl}")
    private Integer stockTtl;
    @Value("${mqconfig.cart_ttl}")
    private Integer cartTtl;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange stockEventExchange(){
        return new TopicExchange(stockEventExchange,true,false);
    }

    @Bean
    public Exchange cartEventExchange(){
        return new TopicExchange(cartEventExchange,true,false);
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
        args.put(ConstantOnlineClass.RABBITMQ_X_MESSAGE_TTL,stockTtl);
        return new Queue(stockReleaseDelayQueue,true,false,false,args);
    }

    /**
     * Delay queue
     * @return
     */
    @Bean
    public Queue cartReleaseDelayQueue(){
        Map<String,Object> args = new HashMap<>();
        args.put(ConstantOnlineClass.RABBITMQ_X_DEAD_LETTER_ROUTING_KEY,cartReleaseRoutingKey);
        args.put(ConstantOnlineClass.RABBITMQ_X_DEAD_LETTER_EXCHANGE,cartEventExchange);
        args.put(ConstantOnlineClass.RABBITMQ_X_MESSAGE_TTL,cartTtl);
        return new Queue(cartReleaseDelayQueue,true,false,false,args);
    }

    /**
     * Dead letter queue
     */
    @Bean
    public Queue stockReleaseQueue(){
        return new Queue(stockReleaseQueue,true,false,false);

    }

    /**
     * Dead letter queue
     */
    @Bean
    public Queue cartReleaseQueue(){
        return new Queue(cartReleaseQueue,true,false,false);

    }

    @Bean
    public Binding stockReleaseBinding(){
        return new Binding(stockReleaseQueue,Binding.DestinationType.QUEUE,stockEventExchange,stockReleaseRoutingKey,null);
    }
    @Bean
    public Binding cartReleaseBinding(){
        return new Binding(cartReleaseQueue,Binding.DestinationType.QUEUE,cartEventExchange,cartReleaseRoutingKey,null);
    }
    @Bean
    public Binding stockReleaseDelayBinding(){
        return new Binding(stockReleaseDelayQueue,Binding.DestinationType.QUEUE,stockEventExchange,stockReleaseDelayRoutingKey,null);
    }

    @Bean
    public Binding cartReleaseDelayBinding(){
        return new Binding(cartReleaseDelayQueue,Binding.DestinationType.QUEUE,cartEventExchange,cartReleaseDelayRoutingKey,null);
    }
}
