package nz.co.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import nz.co.component.PayTool;
import nz.co.config.RabbitMqConfig;
import nz.co.enums.BizCodeEnum;
import nz.co.enums.CouponUseStateEnum;
import nz.co.enums.OrderPayTypeEnum;
import nz.co.enums.OrderStateEnum;
import nz.co.exception.BizCodeException;
import nz.co.feign.AddressFeignService;

import nz.co.feign.CouponFeignService;
import nz.co.feign.ProductFeignService;
import nz.co.interceptor.LoginInterceptor;
import nz.co.mapper.ProductOrderItemMapper;
import nz.co.model.*;
import nz.co.mapper.ProductOrderMapper;
import nz.co.request.GenerateOrderRequest;
import nz.co.service.ProductOrderService;
import nz.co.utils.CommonUtils;
import nz.co.utils.JsonData;
import nz.co.vo.PayInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private ProductOrderItemMapper productOrderItemMapper;
    @Autowired
    private AddressFeignService addressFeignService;

    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private RabbitMqConfig rabbitMqConfig;
    @Autowired
    private RabbitTemplate rabbitTemplate;
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
        String serialNo = CommonUtils.getRandomCode(32);
        JsonData<List<CartItemVO>> jsonData = productFeignService.confirmCartItems(productIds,serialNo);
        if(jsonData.getCode() == 0){
            List<CartItemVO> orderItems = jsonData.getData();
            this.checkPrice(orderItems,generateOrderRequest);
            this.lockCouponRecord(generateOrderRequest,serialNo);
            this.lockProductStock(generateOrderRequest,orderItems,userLoginModel.getId(),serialNo);
            ProductOrderDO productOrderDO = this.saveProductOrder(generateOrderRequest,addressVO,userLoginModel,serialNo);
            this.saveOrderItems(orderItems,productOrderDO);
            ProductOrderMessage productOrderMessage = new ProductOrderMessage();
            productOrderMessage.setSerialNo(serialNo);
            rabbitTemplate.convertAndSend(rabbitMqConfig.getOrderEventExchange(),rabbitMqConfig.getOrderCloseDelayRoutingKey(),productOrderMessage);
            PayInfoVO payInfoVO = new PayInfoVO();
            payInfoVO.setSerailNo(serialNo);
            payInfoVO.setPayFee(productOrderDO.getPayFee());
            payInfoVO.setPayType(productOrderDO.getPayType());
            payInfoVO.setClientType("H5");
            PayTool payTool = new PayTool(payInfoVO);
            String payResult = payTool.pay();
            return JsonData.buildSuccess(orderItems);
        }else{
            log.error("Confirm Cart items failed:"+generateOrderRequest);
            return JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_FAILED);
        }


    }

    private void saveOrderItems(List<CartItemVO> cartItems,ProductOrderDO productOrderDO){
        List<ProductOrderItemDO> orderItemDOList = cartItems.stream().map(obj->{
            ProductOrderItemDO orderItemDO = new ProductOrderItemDO();
            orderItemDO.setOutTradeNo(productOrderDO.getOutTradeNo());
            orderItemDO.setBuyNum(obj.getBuyNum());
            orderItemDO.setCreateTime(new Date());
            orderItemDO.setTotalFee(obj.getTotalFee());
            orderItemDO.setFee(obj.getPrice());
            orderItemDO.setProductId(obj.getProductId());
            orderItemDO.setProductImg(obj.getProductImg());
            orderItemDO.setProductTitle(obj.getProductTitle());
            orderItemDO.setProductOrderId(productOrderDO.getId());
            return orderItemDO;
        }).collect(Collectors.toList());
        productOrderItemMapper.insertBatch(orderItemDOList);
    }

    private ProductOrderDO saveProductOrder(GenerateOrderRequest generateOrderRequest,AddressVO addressVO,UserLoginModel loginUser,String serialNo){
        ProductOrderDO productOrderDO = new ProductOrderDO();
        productOrderDO.setUserId(loginUser.getId());
        productOrderDO.setHeadImg(loginUser.getHeadImg());
        productOrderDO.setNickname(loginUser.getName());
        productOrderDO.setOutTradeNo(serialNo);
        productOrderDO.setReceiverAddress(JSON.toJSONString(addressVO));
        productOrderDO.setCreateTime(new Date());
        productOrderDO.setPayFee(generateOrderRequest.getFeeToPay());
        productOrderDO.setTotalFee(generateOrderRequest.getTotalFee());
        productOrderDO.setPayType(OrderPayTypeEnum.ALIPAY.name());
        productOrderDO.setState(OrderStateEnum.NEW.name());
        productOrderDO.setDel(0);
        productOrderMapper.insert(productOrderDO);
        return productOrderDO;
    }

    private void lockProductStock(GenerateOrderRequest orderRequest,List<CartItemVO> cartItemVOs,Long userId,String serialNo){
        LockProductsRequest lockProductsRequest = new LockProductsRequest();
        lockProductsRequest.setSerialNo(serialNo);
        List<OrderItemRequest> orderItemRequests = new ArrayList<>();
        for(CartItemVO cartItemVO:cartItemVOs){
            OrderItemRequest orderItemRequest = new OrderItemRequest();
            orderItemRequest.setProductTitle(cartItemVO.getProductTitle());
            orderItemRequest.setBuyNum(cartItemVO.getBuyNum());
            orderItemRequest.setProductId(cartItemVO.getProductId());
            orderItemRequest.setSerailNo(orderRequest.getSerialNo());
            orderItemRequest.setUserId(userId);
            orderItemRequests.add(orderItemRequest);
        }
        lockProductsRequest.setOrderItems(orderItemRequests);
        JsonData jsonData = productFeignService.lockProductStock(lockProductsRequest);
        if(jsonData.getCode()!=0){
            throw new BizCodeException(BizCodeEnum.ORDER_LOCK_STOCK_FAILED);
        }
    }

    private void lockCouponRecord(GenerateOrderRequest orderRequest,String serialNo){
        Long couponRecordId = orderRequest.getCouponRecordId();
        if(couponRecordId > 0) {
            LockCouponRecordRequest lockCouponRecordRequest = new LockCouponRecordRequest();
            lockCouponRecordRequest.setSerialNum(serialNo);
            List<Long> couponRecordIds = new ArrayList<>();
            couponRecordIds.add(couponRecordId);
            JsonData<CouponTaskDO> jsonData = couponFeignService.lockCouponRecordBatch(lockCouponRecordRequest);
            if(jsonData.getCode()!=0){
                throw new BizCodeException(BizCodeEnum.COUPON_LOCK_FAIL);
            }
        }
    }

    private void checkPrice(List<CartItemVO> orderItems,GenerateOrderRequest generateOrderRequest){
        BigDecimal totalAmount = new BigDecimal(0);
        for(CartItemVO cartItemVO:orderItems){
            totalAmount = totalAmount.add(cartItemVO.getTotalFee());
        }
        CouponRecordVO couponRecordVO = getCartCouponRecord(generateOrderRequest.getCouponRecordId());
        if(couponRecordVO != null) {

                if (totalAmount.compareTo(couponRecordVO.getConditionPrice()) < 0) {
                    throw new BizCodeException(BizCodeEnum.COUPON_RECORD_DO_NOT_MEET_PRICE_CONDITION);
                }else if(couponRecordVO.getPrice().compareTo(totalAmount)  >0){
                    totalAmount = BigDecimal.ZERO;
                }else{
                    totalAmount = totalAmount.subtract(couponRecordVO.getPrice());
                }

        }
        if(totalAmount.compareTo(generateOrderRequest.getFeeToPay())!=0){
            log.error("check price failed.");
            throw new BizCodeException(BizCodeEnum.COUPON_RECORD_CONFIRM_FAILED);
        }


    }
    private CouponRecordVO getCartCouponRecord(Long couponRecordId){
        if((couponRecordId == null)||(couponRecordId < 0)){
            return null;
        }
        JsonData<CouponRecordVO> jsonData = couponFeignService.detail(couponRecordId);
        if(jsonData.getCode() != 0)
            throw new BizCodeException(BizCodeEnum.COUPON_RECORD_CONFIRM_FAILED);
        CouponRecordVO couponRecordVO = jsonData.getData();
        if(validateCoupon(couponRecordVO)) {
            return couponRecordVO;
        }else{
            throw new BizCodeException(BizCodeEnum.COUPON_RECORD_INVALIDE);
        }
    }
    private boolean validateCoupon(CouponRecordVO couponRecordVO){
        if(couponRecordVO == null)
            return false;
        if(couponRecordVO.getUseState().equalsIgnoreCase(CouponUseStateEnum.COUPON_NEW.name())){
            long current = CommonUtils.getTimestamp();
            long start = couponRecordVO.getStartTime().getTime();
            long end = couponRecordVO.getEndTime().getTime();
            if(current >= start && current <= end){
                return true;
            }
        }
        return false;
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

    @Override
    public boolean closeProductOrder(String serialNo) {
        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>()
        .eq("out_trade_no",serialNo));
        if(productOrderDO == null){
            log.error("Prodcut Order does not exist. "+serialNo);
            return true;
        }
        String orderState = productOrderDO.getState();
        if(OrderStateEnum.PAY.name().equalsIgnoreCase(orderState)){
            log.error("Product order has been paid. "+serialNo);
            return true;
        }
        //TODO third party payment
        String payResult ="";
        if(StringUtils.isBlank(payResult)){
            productOrderMapper.updateOrderState(serialNo,orderState,OrderStateEnum.CANCEL.name());
            log.info("Product order has not been paid. "+serialNo);
            return true;
        }else{
            productOrderMapper.updateOrderState(serialNo,orderState,OrderStateEnum.PAY.name());
            log.info("Product order has been paid. "+serialNo);
            return true;
        }

    }

    private ProductOrderVO beanProcess(ProductOrderDO productOrderDO){
        if(productOrderDO == null) return null;
        ProductOrderVO productOrderVO = new ProductOrderVO();
        BeanUtils.copyProperties(productOrderDO,productOrderVO);
        return productOrderVO;
    }
}
