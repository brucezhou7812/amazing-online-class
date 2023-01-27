package nz.co.service.impl;

import lombok.extern.slf4j.Slf4j;
import nz.co.model.ProductOrderDO;
import nz.co.mapper.ProductOrderMapper;
import nz.co.request.GenerateOrderRequest;
import nz.co.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nz.co.utils.JsonData;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-24
 */
@Service
@Slf4j
public class ProductOrderServiceImpl implements ProductOrderService {

    @Override
    public JsonData generateOrder(GenerateOrderRequest generateOrderRequest) {
        return null;
    }
}
