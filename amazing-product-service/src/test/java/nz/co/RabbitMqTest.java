package nz.co;

import lombok.extern.slf4j.Slf4j;
import nz.co.config.RabbitMqConfig;
import nz.co.model.OrderItemRequest;
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
    @Test
    public void testSendRestoreCartItemMessage(){
        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setUserId(1L);
        orderItemRequest.setSerailNo("123456abc");
        orderItemRequest.setProductId(1L);
        orderItemRequest.setBuyNum(2);
       // orderItemRequest.setProductTitle(obj.getProductTitle());
        rabbitTemplate.convertAndSend(rabbitMqConfig.getCartEventExchange(),rabbitMqConfig.getCartReleaseDelayRoutingKey(),orderItemRequest);
    }
}
