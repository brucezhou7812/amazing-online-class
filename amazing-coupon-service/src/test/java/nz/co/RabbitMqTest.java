package nz.co;

import nz.co.model.CouponRecordMessage;
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
    @Test
    public void testReleaseCouponRecord(){
        CouponRecordMessage couponRecordMessage = new CouponRecordMessage();
        couponRecordMessage.setCouponTaskId(1L);
        couponRecordMessage.setSerialNum("123456abc");
        rabbitTemplate.convertAndSend("coupon.event.exchange","coupon.release.delay.routing.key",couponRecordMessage);
    }
    @Test
    public void testException(){
        for(int i=0;i<10;i++){
            System.out.println("i = "+i);
            if(i%5 == 4)
                throw new RuntimeException();
        }
    }
}
