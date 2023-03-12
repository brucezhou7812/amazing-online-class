package nz.co.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import nz.co.enums.CartTaskLockStateEnum;
import nz.co.feign.OrderFeignService;
import nz.co.model.CartTaskDO;
import nz.co.model.OrderItemRequest;
import nz.co.model.ProductRecordMessage;
import nz.co.request.AddCartRequest;
import nz.co.service.CartService;
import nz.co.service.CartTaskService;
import nz.co.service.ProductService;
import nz.co.utils.CommonUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RabbitListener(queues = "${mqconfig.cart_release_queue}")
public class ProductCartMqListener {

    @Autowired
    private CartService cartService;
    @Autowired
    private CartTaskService cartTaskService;
    @Autowired
    private OrderFeignService orderFeignService;
    @RabbitHandler
    public void restoreCartItem(OrderItemRequest orderItemRequest, Message message, Channel channel) throws IOException {
        log.info(orderItemRequest+" has been received.");
        long messageTag = message.getMessageProperties().getDeliveryTag();
        CommonUtils.setTokenToContextHolder(message);
        boolean flag = cartService.restoreCartItem(orderItemRequest);
        try{
            if(flag){
                channel.basicAck(messageTag,false);
            } else {
                log.error("restore cart item failed:" + orderItemRequest);
                channel.basicReject(messageTag, false);
            }
        } catch (IOException e) {
            log.error("Exception is thrown when restore cart item:" + orderItemRequest);
            channel.basicReject(messageTag, true);
        }

    }

}
