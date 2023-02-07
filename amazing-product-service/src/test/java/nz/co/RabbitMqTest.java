package nz.co;

import lombok.extern.slf4j.Slf4j;
import nz.co.config.RabbitMqConfig;
import nz.co.model.ProductRecordMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = ProductApplication.class)
@RunWith(value= SpringRunner.class)
@Slf4j
public class RabbitMqTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMqConfig rabbitMqConfig;
    @Test
    public void testSendMessage(){
        rabbitTemplate.convertAndSend(rabbitMqConfig.getStockEventExchange(),rabbitMqConfig.getStockReleaseDelayRoutingKey(),"hello,this is a rabbitmq for stock");
    }
    @Test
    public void testSendProductStockMessage(){
        ProductRecordMessage productRecordMessage = new ProductRecordMessage();
        productRecordMessage.setSerialNo("123456abc");
        productRecordMessage.setProductTaskId(1L);
        rabbitTemplate.convertAndSend(rabbitMqConfig.getStockEventExchange(),rabbitMqConfig.getStockReleaseDelayRoutingKey(),productRecordMessage);

    }
}
