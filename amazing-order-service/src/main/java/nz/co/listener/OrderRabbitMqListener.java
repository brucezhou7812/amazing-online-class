package nz.co.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import nz.co.model.ProductOrderMessage;
import nz.co.service.ProductOrderService;
import nz.co.utils.CommonUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;

import java.io.IOException;


@RabbitListener(queues = "${mqconfig.order_close_queue}")
@Slf4j
@Component
public class OrderRabbitMqListener {
    @Autowired
    private ProductOrderService productOrderService;
    @RabbitHandler
    public void closeProductOrder(ProductOrderMessage orderMessage, Message message, Channel channel) throws IOException {
        log.info("Message has been received: "+orderMessage);
        long messageTag = message.getMessageProperties().getDeliveryTag();
        CommonUtils.setTokenToContextHolder(message);
        boolean flag = productOrderService.closeProductOrder(orderMessage.getSerialNo());
        try {
            if (flag) {
                log.info("Message has been consumed:"+orderMessage);
                channel.basicAck(messageTag, false);
            }else{
                log.info("Message failed to be consumed:"+orderMessage+" ,reentry to rabbitmq");
                channel.basicReject(messageTag,true);
            }
        }catch(IOException e){
            log.info("Message failed to be consumed:"+orderMessage+" ,reentry to rabbitmq");
            channel.basicReject(messageTag,true);
        }
    }
}
