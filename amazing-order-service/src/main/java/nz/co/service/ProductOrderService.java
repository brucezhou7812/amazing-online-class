package nz.co.service;

import nz.co.request.GenerateOrderRequest;
import nz.co.utils.JsonData;
import nz.co.model.ProductOrderVO;

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
}
