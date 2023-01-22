package nz.co.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nz.co.service.CartService;
import nz.co.utils.JsonData;
import nz.co.request.AddCartRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags="Product service")
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
}
