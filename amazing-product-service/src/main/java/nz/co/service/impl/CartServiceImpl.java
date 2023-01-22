package nz.co.service.impl;

import com.alibaba.fastjson.JSON;
import nz.co.constant.ConstantOnlineClass;
import nz.co.enums.BizCodeEnum;
import nz.co.exception.BizCodeException;
import nz.co.interceptor.LoginInterceptor;
import nz.co.model.UserLoginModel;
import nz.co.service.CartService;
import nz.co.request.AddCartRequest;
import nz.co.service.ProductService;
import nz.co.vo.CartItemVO;
import nz.co.vo.ProductVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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