package nz.co.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.domain.PageInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import nz.co.component.PayTool;
import nz.co.component.RabbitMQMessagePostProcessor;
import nz.co.config.RabbitMqConfig;
import nz.co.constant.ConstantOnlineClass;
import nz.co.enums.*;
import nz.co.exception.BizCodeException;
import nz.co.feign.AddressFeignService;

import nz.co.feign.CouponFeignService;
import nz.co.feign.ProductFeignService;
import nz.co.interceptor.LoginInterceptor;
import nz.co.mapper.ProductOrderItemMapper;
import nz.co.model.*;
import nz.co.mapper.ProductOrderMapper;
import nz.co.request.GenerateOrderRequest;
import nz.co.request.RepayOrderRequest;
import nz.co.service.ProductOrderService;
import nz.co.utils.CommonUtils;
import nz.co.utils.JsonData;
import nz.co.vo.PayInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.spring.web.json.Json;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    @Transactional
    public JsonData generateOrder(GenerateOrderRequest generateOrderRequest) {
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        if(userLoginModel == null){
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_NOT_LOGIN);
        }
        String token = generateOrderRequest.getToken();
        if(StringUtils.isBlank(token)){
            throw new BizCodeException(BizCodeEnum.ORDER_TOKEN_NOT_EXIST);
        }
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        String key = String.format(ConstantOnlineClass.KEY_IN_REDIS_ORDER_TOKEN,userLoginModel.getId());
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script,Long.class),Arrays.asList(key),token);
        if(result == 0L){
            throw new BizCodeException(BizCodeEnum.ORDER_TOKEN_INVALID);
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
            rabbitTemplate.convertAndSend(rabbitMqConfig.getOrderEventExchange(), rabbitMqConfig.getOrderCloseDelayRoutingKey(), productOrderMessage, new RabbitMQMessagePostProcessor());
            PayInfoVO payInfoVO = new PayInfoVO();
            payInfoVO.setSerailNo(serialNo);
            payInfoVO.setPayFee(generateOrderRequest.getFeeToPay());
            payInfoVO.setPayType(generateOrderRequest.getPayType());
            payInfoVO.setClientType(generateOrderRequest.getClientType());
            payInfoVO.setTitle(orderItems.get(0).getProductTitle());
            payInfoVO.setTimeoutMills(ConstantOnlineClass.EXPIRE_TIME_FOR_ORDER_PAYMENT);
            PayTool payTool = new PayTool(payInfoVO);
            String payResult = payTool.pay();
            if(StringUtils.isNotBlank(payResult)) {
                log.info("Alipay success");
                return JsonData.buildSuccess(payResult);
            }else{
                log.error("Alipay failed");
                return JsonData.buildResult(BizCodeEnum.ORDER_PAID_FAILED);
            }
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
        productOrderDO.setOrderType(OrderTypeEnum.AVERAGE.name());
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
            lockCouponRecordRequest.setCouponRecordIds(couponRecordIds);
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
        if(couponRecordVO.getUseState().equalsIgnoreCase(CouponUseStateEnum.COUPON_NEW.getDesc())){
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
        PayInfoVO payInfoVO = new PayInfoVO();
        payInfoVO.setSerailNo(productOrderDO.getOutTradeNo());
        payInfoVO.setPayType(productOrderDO.getPayType());
        payInfoVO.setPayFee(productOrderDO.getPayFee());
        PayTool payTool = new PayTool(payInfoVO);
        payResult = payTool.queryPayStatus();
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

    @Override
    public JsonData handleOrderCallback(OrderPayTypeEnum payType, Map<String, String> params) {
        if(OrderPayTypeEnum.ALIPAY == payType){
            String serialNo = params.get(ConstantOnlineClass.OUT_TRADE_NO);
            String status = params.get(ConstantOnlineClass.TRADE_SATATUS);
            if(status.equalsIgnoreCase(ConstantOnlineClass.TRADE_FINISHED) || status.equalsIgnoreCase(ConstantOnlineClass.TRADE_SUCCESS)){
                productOrderMapper.updateOrderState(serialNo,OrderStateEnum.PAY.name(),OrderStateEnum.PAY.name());
                return JsonData.buildSuccess();
            }

        }else if(OrderPayTypeEnum.WECHAT == payType){

        }
        return JsonData.buildResult(BizCodeEnum.ORDER_CALLBACK_FAILED);
    }


    @Override
    public JsonData page(int page, int size, String state) {
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        Page<ProductOrderDO> pageInfo = new Page<>(page,size);
        IPage<ProductOrderDO> ipage = null;
        if(StringUtils.isNotBlank(state)) {
            ipage = productOrderMapper.selectPage(pageInfo, new QueryWrapper<ProductOrderDO>()
                    .eq("user_id", userLoginModel.getId())
                    .eq("state", state));
        }else{
            ipage = productOrderMapper.selectPage(pageInfo, new QueryWrapper<ProductOrderDO>()
                    .eq("user_id", userLoginModel.getId()));
        }
        List<ProductOrderDO> productOrderDOList = ipage.getRecords();
        List<ProductOrderVO> productOrderVOList = productOrderDOList.stream().map(obj->{
            ProductOrderVO productOrderVO = new ProductOrderVO();
            BeanUtils.copyProperties(obj,productOrderVO);
            Long productOrderId = productOrderVO.getId();
            List<ProductOrderItemDO> orderItemDOs = productOrderItemMapper.selectList(new QueryWrapper<ProductOrderItemDO>()
            .eq("product_order_id",productOrderId));
            List<ProductOrderItemVO> orderItemVOS = orderItemDOs.stream().map(item->{
                ProductOrderItemVO orderItemVO = new ProductOrderItemVO();
                BeanUtils.copyProperties(item,orderItemVO);
                return orderItemVO;
            }).collect(Collectors.toList());
            productOrderVO.setOrderItems(orderItemVOS);
            return productOrderVO;
        }).collect(Collectors.toList());
        Map<String,Object> pageMap = new HashMap<>();
        pageMap.put(ConstantOnlineClass.PAGINATION_TOTAL_PAGES,ipage.getPages());
        pageMap.put(ConstantOnlineClass.PAGINATION_TOTAL_RECORDS,ipage.getTotal());
        pageMap.put(ConstantOnlineClass.PAGINATION_CURRENT_DATA,productOrderVOList);

        return JsonData.buildSuccess(pageMap);
    }

    @Override
    @Transactional
    public JsonData repay(RepayOrderRequest repayOrderRequest) {
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>()
        .eq("user_id",userLoginModel.getId())
        .eq("out_trade_no",repayOrderRequest.getSerialNo()));
        if(productOrderDO == null){
            log.error("Order does not exist, serial no is :"+repayOrderRequest.getSerialNo());
            return JsonData.buildResult(BizCodeEnum.ORDER_NOT_EXIST);
        }
        if(!productOrderDO.getState().equalsIgnoreCase(OrderStateEnum.NEW.name())){
            log.error("Order state is invalid, State: "+productOrderDO.getState()+" serial no is :"+repayOrderRequest.getSerialNo());
            return JsonData.buildResult(BizCodeEnum.ORDER_STATE_INVALID);
        }

        long timeElapse = CommonUtils.getTimestamp()-productOrderDO.getCreateTime().getTime();
        long timeTogo = timeElapse + 70*1000;
        if(timeTogo > ConstantOnlineClass.EXPIRE_TIME_FOR_ORDER_PAYMENT){
            log.error("Order is expired, serial no is :"+repayOrderRequest.getSerialNo());
            return JsonData.buildResult(BizCodeEnum.ORDER_TIMEOUT);
        }
        PayInfoVO payInfoVO = new PayInfoVO();
        payInfoVO.setSerailNo(productOrderDO.getOutTradeNo());
        payInfoVO.setPayFee(productOrderDO.getPayFee());
        payInfoVO.setPayType(repayOrderRequest.getPayType());
        payInfoVO.setClientType(repayOrderRequest.getClientType());
        payInfoVO.setTitle(productOrderDO.getNickname());
        payInfoVO.setTimeoutMills(ConstantOnlineClass.EXPIRE_TIME_FOR_ORDER_PAYMENT-timeTogo);
        PayTool payTool = new PayTool(payInfoVO);
        String payResult = payTool.pay();
        if(StringUtils.isNotBlank(payResult)) {
            log.info("Alipay success");
            return JsonData.buildSuccess(payResult);
        }else{
            log.error("Alipay failed");
            return JsonData.buildResult(BizCodeEnum.ORDER_PAID_FAILED);
        }
    }

    @Override
    public JsonData<String> getOrderToken() {
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        String key = String.format(ConstantOnlineClass.KEY_IN_REDIS_ORDER_TOKEN,userLoginModel.getId());
        String token = CommonUtils.getRandomCode(32);
        redisTemplate.opsForValue().set(key,token,30, TimeUnit.MINUTES);
        return JsonData.buildSuccess(token);
    }

    private ProductOrderVO beanProcess(ProductOrderDO productOrderDO){
        if(productOrderDO == null) return null;
        ProductOrderVO productOrderVO = new ProductOrderVO();
        BeanUtils.copyProperties(productOrderDO,productOrderVO);
        return productOrderVO;
    }
}
