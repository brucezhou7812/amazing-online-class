package nz.co.listener;


import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import nz.co.model.CouponRecordMessage;

import nz.co.service.CouponRecordService;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RabbitListener(queues = "${mqconfig.coupon_release_queue}")
public class CouponMqListener {

    @Autowired
    private CouponRecordService couponRecordService;
    @RabbitHandler
    public void receiveTestMsg(String testMsg, Message message, Channel channel){
        log.info("Message has been received: "+testMsg);
        long messageTag = message.getMessageProperties().getDeliveryTag();
        try {
            channel.basicAck(messageTag,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @RabbitHandler
    public void releaseCouponRecord(CouponRecordMessage recordMessage, Message message, Channel channel) throws IOException {
        log.info("Message has been received: "+recordMessage);
        long messageTag = message.getMessageProperties().getDeliveryTag();
        boolean flag = couponRecordService.releaseCouponRecord(recordMessage);
        try {
            if (flag) {
                channel.basicAck(messageTag, false);
            } else {
                log.error("release coupon failed:" + recordMessage);
                channel.basicReject(messageTag, true);
            }
        }catch (IOException e){
            log.error("Exception is thrown when releasing coupon:" + recordMessage);
            channel.basicReject(messageTag, true);
        }
    }
}
