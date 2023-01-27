package nz.co.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nz.co.enums.BizCodeEnum;
import nz.co.request.UpdateCartRequest;
import nz.co.service.CartService;
import nz.co.utils.JsonData;
import nz.co.request.AddCartRequest;
import nz.co.vo.CartItemVO;
import nz.co.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags="Product Service: Cart")
@RequestMapping("/api/cart/v1")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping(value="add")
    @ApiOperation(value="Add product to cart")
    public JsonData addToCart(@ApiParam("the item added to the cart")@RequestBody AddCartRequest addCartRequest){
        cartService.addToCart(addCartRequest);
        return JsonData.buildSuccess();
    }
    @DeleteMapping(value="clearCart")
    @ApiOperation(value="clear cart")
    public JsonData clearCart(){
        cartService.clearCart();
        return JsonData.buildSuccess();
    }

    @GetMapping(value="listCart")
    @ApiOperation(value="list all items in cart")
    public JsonData listCart(){
        CartVO result = cartService.listCart();
        return result==null?JsonData.buildResult(BizCodeEnum.CART_IS_EMPTY):JsonData.buildSuccess(result);
    }

    @DeleteMapping(value="deleteItem/{product_id}")
    @ApiOperation(value="delete item from cart")
    public JsonData deleteItem(@ApiParam(value="product id")@PathVariable("product_id")Long productID){
        CartItemVO cartItemVO = cartService.deleteItem(productID);
        return cartItemVO == null ?JsonData.buildResult(BizCodeEnum.PRODUCT_NOT_EXIST):JsonData.buildSuccess(cartItemVO);
    }

    @PostMapping(value="updateItem")
    @ApiOperation(value="update item in cart")
    public JsonData updateItem(@ApiParam(value="update the number of cart item")@RequestBody UpdateCartRequest request){
        CartItemVO cartItemVO = cartService.updateCart(request);
        return cartItemVO == null ?JsonData.buildResult(BizCodeEnum.PRODUCT_NOT_EXIST):JsonData.buildSuccess(cartItemVO);
    }

}
