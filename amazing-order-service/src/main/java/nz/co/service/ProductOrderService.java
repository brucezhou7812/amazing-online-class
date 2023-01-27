package nz.co.service;

import nz.co.model.ProductOrderDO;
import com.baomidou.mybatisplus.extension.service.IService;
import nz.co.request.GenerateOrderRequest;
import nz.co.utils.JsonData;

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
}
