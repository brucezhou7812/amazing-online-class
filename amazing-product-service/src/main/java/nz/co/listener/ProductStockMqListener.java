package nz.co.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import nz.co.enums.CartTaskLockStateEnum;
import nz.co.model.CartTaskDO;
import nz.co.model.OrderItemRequest;
import nz.co.model.ProductRecordMessage;
import nz.co.request.AddCartRequest;
import nz.co.service.CartService;
import nz.co.service.CartTaskService;
import nz.co.service.ProductService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RabbitListener(queues = "${mqconfig.stock_release_queue}")
public class ProductStockMqListener {
    @Autowired
    private ProductService productService;

    @RabbitHandler
    public void releaseProductStock(ProductRecordMessage recordMessage, Message message, Channel channel) throws IOException {
        log.info("Message has been received: "+recordMessage);
        long messageTag = message.getMessageProperties().getDeliveryTag();
        boolean flag = productService.releaseStock(recordMessage);
        try {
            if (flag) {
                channel.basicAck(messageTag, false);
            } else {
                log.error("release stock failed:" + recordMessage);
                channel.basicReject(messageTag, false);
            }
        }catch (IOException e){
            log.error("Exception is thrown when releasing stock:" + recordMessage);
            channel.basicReject(messageTag, true);
        }

    }
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


}
