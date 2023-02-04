package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import nz.co.model.ProductOrderDO;
import nz.co.mapper.ProductOrderMapper;
import nz.co.request.GenerateOrderRequest;
import nz.co.service.ProductOrderService;
import nz.co.utils.JsonData;
import nz.co.model.ProductOrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ProductOrderMapper productOrderMapper;
    @Override
    public JsonData generateOrder(GenerateOrderRequest generateOrderRequest) {
        return null;
    }

    @Override
    public String queryOrderState(String serialNum) {
        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>()
        .eq("out_trade_no",serialNum));
        //ProductOrderVO productOrderVO = beanProcess(productOrderDO);
        return productOrderDO.getState();
    }

    private ProductOrderVO beanProcess(ProductOrderDO productOrderDO){
        if(productOrderDO == null) return null;
        ProductOrderVO productOrderVO = new ProductOrderVO();
        BeanUtils.copyProperties(productOrderDO,productOrderVO);
        return productOrderVO;
    }
}
