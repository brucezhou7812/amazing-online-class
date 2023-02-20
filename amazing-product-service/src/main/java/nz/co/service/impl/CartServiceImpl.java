package nz.co.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import nz.co.config.RabbitMqConfig;
import nz.co.constant.ConstantOnlineClass;
import nz.co.enums.BizCodeEnum;
import nz.co.enums.CartTaskLockStateEnum;
import nz.co.enums.OrderStateEnum;
import nz.co.exception.BizCodeException;
import nz.co.feign.OrderFeignService;
import nz.co.interceptor.LoginInterceptor;
import nz.co.model.CartTaskDO;
import nz.co.model.OrderItemRequest;
import nz.co.model.UserLoginModel;
import nz.co.request.UpdateCartRequest;
import nz.co.service.CartService;
import nz.co.request.AddCartRequest;
import nz.co.service.CartTaskService;
import nz.co.service.ProductService;
import nz.co.utils.JsonData;
import nz.co.model.CartItemVO;
import nz.co.vo.CartVO;
import nz.co.model.ProductVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductService productService;
    @Autowired
    private RabbitMqConfig rabbitMqConfig;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private CartTaskService cartTaskService;
    @Autowired
    private OrderFeignService orderFeignService;
    @Override
    public void addToCart(AddCartRequest addCartRequest) {
        Long productIdNum = addCartRequest.getProductId();
        String productId = Long.toString(productIdNum);
        Integer buyNum = addCartRequest.getBuyNum();
        BoundHashOperations<String, Object, Object> cart = getCart();
        Object cacheObj = cart.get(productId);
        String result = "";
        if (cacheObj != null) {
            result = (String) cacheObj;
        }
        if (StringUtils.isBlank(result)) {
            ProductVO productVO = productService.listProductDetailById(productIdNum);
            if (productVO == null) throw new BizCodeException(BizCodeEnum.PRODUCT_NOT_EXIST);
            CartItemVO cartItem = new CartItemVO();
            cartItem.setBuyNum(buyNum);
            cartItem.setProductId(productIdNum);
            cartItem.setProductImg(productVO.getCoverImg());
            cartItem.setProductTitle(productVO.getTitle());
            cartItem.setPrice(productVO.getPrice());
            String cartString = JSON.toJSONString(cartItem);
            cart.put(productId, cartString);
        } else {
            CartItemVO cartItem = JSON.parseObject(result, CartItemVO.class);
            cartItem.setBuyNum(buyNum + cartItem.getBuyNum());
            String cartString = JSON.toJSONString(cartItem);
            cart.put(productId, cartString);
        }

    }

    @Override
    public void clearCart() {
        redisTemplate.delete(getCartKey());
    }

    @Override
    public CartItemVO deleteItem(Long productID) {
        BoundHashOperations<String,Object,Object> myCart = getCart();
        String strCartItem = (String)myCart.get(Long.toString(productID));
        CartItemVO cartItemVO = JSON.parseObject(strCartItem,CartItemVO.class);
        myCart.delete(Long.toString(productID));
        return cartItemVO;
    }

    @Override
    public CartVO listCart() {
        BoundHashOperations<String,Object,Object> cart = this.getCart();
        List<Object> stringCartItems = cart.values();
        if(cart.size() == 0) return null;
        List<String> strProductIds = cart.keys().stream().map(obj->{
            return (String)obj;
        }).collect(Collectors.toList());
        List<CartItemVO> cartItems = stringCartItems.stream().map(obj->{
            String strCarItems = (String)obj;
            CartItemVO cartItemVO = JSON.parseObject(strCarItems,CartItemVO.class);
            return cartItemVO;
        }).collect(Collectors.toList());
        checkAndUpdatePrice(cartItems,strProductIds);
        CartVO cartVO = new CartVO();
        cartVO.setCartItems(cartItems);
        cartVO.setUserId(LoginInterceptor.threadLocalUserLoginModel.get().getId());
        BigDecimal feeToPay = new BigDecimal(0);

        for(CartItemVO cartItemVO:cartItems){
            feeToPay = feeToPay.add(cartItemVO.getTotalFee());
        }
        cartVO.setFeeToPay(feeToPay);

        return cartVO;
    }

    private List<CartItemVO> getCartItems(){
        BoundHashOperations<String,Object,Object> cart = this.getCart();
        List<Object> stringCartItems = cart.values();
        if(cart.size() == 0) return null;
        List<CartItemVO> cartItems = stringCartItems.stream().map(obj->{
            String strCarItems = (String)obj;
            CartItemVO cartItemVO = JSON.parseObject(strCarItems,CartItemVO.class);
            return cartItemVO;
        }).collect(Collectors.toList());
        return cartItems;
    }

    private void checkAndUpdatePrice(List<CartItemVO> sources,List<String>productIds){
        List<ProductVO> products = productService.listProductsBatch(productIds);
        Map<Long,ProductVO> productMap = products.stream().collect(Collectors.toMap(ProductVO::getId, Function.identity()));
        sources.stream().map(obj->{
            ProductVO productVO = productMap.get(obj.getProductId());
            if(productVO == null) return obj;
            obj.setPrice(productVO.getPrice());
            obj.setProductTitle(productVO.getTitle());
            obj.setProductImg(productVO.getCoverImg());
            return obj;
        }).collect(Collectors.toList());
    }


    @Override
    public CartItemVO updateCart(UpdateCartRequest request) {
        if(request == null)
            return null;
        Long productId = request.getProductId();
        Integer buyNum = request.getBuyNum();
        String strProductId = Long.toString(productId);
        BoundHashOperations<String,Object,Object> myCart = this.getCart();
        String strCartItem = (String)myCart.get(strProductId);
        if(strCartItem == null) return null;
        CartItemVO cartItem = JSON.parseObject(strCartItem,CartItemVO.class);
        cartItem.setBuyNum(buyNum);
        strCartItem = JSON.toJSONString(cartItem);
        myCart.put(strProductId,strCartItem);
        return cartItem;
    }

    @Override
    public JsonData<List<CartItemVO>> confirmCartItems(List<Long> productIds,String serialNo) {
        List<CartItemVO> cartItems = getCartItems();
        if(cartItems == null || cartItems.size()==0)
            return JsonData.buildResult(BizCodeEnum.CART_IS_EMPTY);
        List<String> strProductIds = productIds.stream().map(obj->{
            String strId = Long.toString(obj);
            return strId;
        }).collect(Collectors.toList());
        checkAndUpdatePrice(cartItems,strProductIds);

        List<CartItemVO> confirmItems = cartItems.stream().filter(obj->{
            Long productId = obj.getProductId();
            boolean exist = productIds.contains(productId);
            if(exist){
                OrderItemRequest orderItemRequest = new OrderItemRequest();
                Long userId = LoginInterceptor.threadLocalUserLoginModel.get().getId();
                orderItemRequest.setUserId(userId);
                orderItemRequest.setSerailNo(serialNo);
                orderItemRequest.setProductId(productId);
                orderItemRequest.setBuyNum(obj.getBuyNum());
                orderItemRequest.setProductTitle(obj.getProductTitle());
                rabbitTemplate.convertAndSend(rabbitMqConfig.getCartEventExchange(),rabbitMqConfig.getCartReleaseDelayRoutingKey(),orderItemRequest);
                this.insertCartTask(obj,serialNo);
                this.deleteItem(productId);
                return true;
            }else{
                return false;
            }
        }).collect(Collectors.toList());

        return JsonData.buildSuccess(confirmItems);
    }

    @Override
    public boolean restoreCartItem(OrderItemRequest orderItemRequest) {
        String serialNo = orderItemRequest.getSerailNo();
        Long productId = orderItemRequest.getProductId();
        Long userId = orderItemRequest.getUserId();
        if(orderItemRequest == null){
            log.error("OrderItemRequest is null");
            return true;
        }
        CartTaskDO cartTaskDO = cartTaskService.listCartTask(serialNo,productId,userId);
        if(cartTaskDO == null){
            log.error("Cart Task does not exist: "+orderItemRequest);
            return true;
        }
        String state = cartTaskDO.getLockState();
        if(CartTaskLockStateEnum.LOCKED.name().equalsIgnoreCase(state)) {
            JsonData<String> jsonData = orderFeignService.queryOrderStateBySerialNo(serialNo);
            if(jsonData.getCode() == 0) {
                String orderState = jsonData.getData();
                if(OrderStateEnum.PAY.name().equalsIgnoreCase(orderState)){
                    cartTaskDO.setLockState(CartTaskLockStateEnum.FINISHED.name());
                    cartTaskService.updateLockState(cartTaskDO);
                    log.info("update cart task state to FINISHED: " + cartTaskDO);
                    return true;
                }else if(OrderStateEnum.NEW.name().equalsIgnoreCase(orderState)){

                    log.info("The order has not been paid: " + cartTaskDO);
                    return false;
                }
            }
            log.info("The order does not exist or has been cancelled." +cartTaskDO);
            AddCartRequest addCartRequest = new AddCartRequest();
            addCartRequest.setProductId(productId);
            addCartRequest.setBuyNum(orderItemRequest.getBuyNum());
            addToCart(addCartRequest);
            log.info("restore cart item: " + addCartRequest);
            cartTaskDO.setLockState(CartTaskLockStateEnum.CANCELLED.name());
            cartTaskService.updateLockState(cartTaskDO);
            log.info("update cart task state to CANCELLED: " + cartTaskDO);
            return true;

        }else{
            log.error("The Cart Task state is not locked :"+state);
            return true;
        }
    }

    private int insertCartTask(CartItemVO cartItemVO,String serialNo){
        CartTaskDO cartTaskDO = new CartTaskDO();
        Long userId = LoginInterceptor.threadLocalUserLoginModel.get().getId();
        cartTaskDO.setUserId(userId);
        cartTaskDO.setBuyNum(cartItemVO.getBuyNum());
        cartTaskDO.setLockState(CartTaskLockStateEnum.LOCKED.name());
        cartTaskDO.setProductId(cartItemVO.getProductId());
        cartTaskDO.setProductName(cartItemVO.getProductTitle());
        cartTaskDO.setSerialNum(serialNo);
        cartTaskDO.setCreateTime(new Date());
        return cartTaskService.insert(cartTaskDO);
    }

    private String getCartKey() {
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        String key = null;
        if (userLoginModel != null) {
            key = String.format(ConstantOnlineClass.KEY_IN_REDIS_CART, userLoginModel.getId());
        }
        return key;
    }
    private BoundHashOperations initCart(Long productId){
        String key = getCartKey();
        redisTemplate.opsForHash().put(key,productId," ");
        return redisTemplate.boundHashOps(key);
    }
    private BoundHashOperations<String, Object, Object>  getCart() {
        String key = getCartKey();
        if (key != null)
            return redisTemplate.boundHashOps(key);
        else
            return null;
    }
}