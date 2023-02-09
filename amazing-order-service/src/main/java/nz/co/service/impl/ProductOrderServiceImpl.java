package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import nz.co.enums.BizCodeEnum;
import nz.co.feign.AddressFeignService;
import nz.co.feign.CartFeignService;
import nz.co.interceptor.LoginInterceptor;
import nz.co.model.*;
import nz.co.mapper.ProductOrderMapper;
import nz.co.request.GenerateOrderRequest;
import nz.co.service.ProductOrderService;
import nz.co.utils.JsonData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Autowired
    private AddressFeignService addressFeignService;
    @Autowired
    private CartFeignService cartFeignService;
    @Override
    public JsonData generateOrder(GenerateOrderRequest generateOrderRequest) {
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        if(userLoginModel == null){
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_NOT_LOGIN);
        }
        Long addressId = generateOrderRequest.getAddressId();
        AddressVO addressVO = this.getUserAddress(addressId);
        if(addressVO == null){
            log.error("Can not get post address.");
            return JsonData.buildResult(BizCodeEnum.ADDRESS_NOT_EXIST);
        }
        List<Long> productIds = generateOrderRequest.getProductIds();
        JsonData<List<CartItemVO>> jsonData = cartFeignService.confirmCartItems(productIds);
        if(jsonData.getCode() == 0){
           List<CartItemVO> orderItems = jsonData.getData();
            return JsonData.buildSuccess(orderItems);
        }else{
            log.error("Confirm Cart items failed:"+generateOrderRequest);
            return JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_FAILED);
        }

    }

    private AddressVO getUserAddress(Long addressId){
        JsonData<AddressVO> jsonData = addressFeignService.detail(addressId);
        if(jsonData.getCode() == 0){
            AddressVO addressVO = jsonData.getData();
            log.info("The post address is :" +addressVO);
            return addressVO;
        }else{
            log.error("Can not get post address.");
            return null;
        }
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
