package nz.co;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = CouponApplication.class)
@RunWith(SpringRunner.class)
public class RabbitMqTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    public void testSendMessage(){
        rabbitTemplate.convertAndSend("coupon.event.exchange","coupon.release.delay.routing.key","hello,this the first message of RabbitMQ");
    }
}
