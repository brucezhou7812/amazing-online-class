package nz.co;

import lombok.extern.slf4j.Slf4j;
import nz.co.config.RabbitMqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = OrderApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class TestRabbitMq {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMqConfig rabbitMqConfig;
    @Test
    public void testSendStringMsg(){
        String message = "Hello Order Rabbit Queue";
        rabbitTemplate.convertAndSend(rabbitMqConfig.getOrderEventExchange(),rabbitMqConfig.getOrderCloseDelayRoutingKey(),message);
    }
}
