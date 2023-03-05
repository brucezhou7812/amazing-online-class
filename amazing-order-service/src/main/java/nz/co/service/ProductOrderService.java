package nz.co.service;

import nz.co.enums.OrderPayTypeEnum;
import nz.co.request.GenerateOrderRequest;
import nz.co.utils.JsonData;
import nz.co.model.ProductOrderVO;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-24
 */
public interface ProductOrderService{

    JsonData generateOrder(GenerateOrderRequest generateOrderRequest);

    String queryOrderState(String serialNum);

    boolean closeProductOrder(String serialNo);

    JsonData handleOrderCallback(OrderPayTypeEnum alipay, Map<String, String> params);
}
