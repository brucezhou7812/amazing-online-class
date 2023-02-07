package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import nz.co.enums.BizCodeEnum;
import nz.co.feign.AddressFeignService;
import nz.co.interceptor.LoginInterceptor;
import nz.co.model.AddressVO;
import nz.co.model.ProductOrderDO;
import nz.co.mapper.ProductOrderMapper;
import nz.co.model.UserLoginModel;
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
    @Autowired
    private AddressFeignService addressFeignService;
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
        return JsonData.buildSuccess();
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
