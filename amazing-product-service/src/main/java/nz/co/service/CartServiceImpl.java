package nz.co.service;

import com.alibaba.fastjson.JSON;
import nz.co.constant.ConstantOnlineClass;
import nz.co.enums.BizCodeEnum;
import nz.co.exception.BizCodeException;
import nz.co.interceptor.LoginInterceptor;
import nz.co.model.UserLoginModel;
import nz.co.request.UpdateCartRequest;
import nz.co.service.CartService;
import nz.co.request.AddCartRequest;
import nz.co.service.ProductService;
import nz.co.vo.CartItemVO;
import nz.co.vo.CartVO;
import nz.co.vo.ProductVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductService productService;

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

    private void checkAndUpdatePrice(List<CartItemVO> sources,List<String>productIds){
        List<ProductVO> products = productService.listProductsBatch(productIds);
        Map<Long,ProductVO> productMap = products.stream().collect(Collectors.toMap(ProductVO::getId, Function.identity()));
        sources.stream().map(obj->{
            ProductVO productVO = productMap.get(obj.getProductId());
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