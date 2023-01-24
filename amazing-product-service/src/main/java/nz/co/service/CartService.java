package nz.co.service;

import nz.co.request.AddCartRequest;
import nz.co.request.UpdateCartRequest;
import nz.co.vo.CartItemVO;
import nz.co.vo.CartVO;

import java.util.List;

public interface CartService {
    void addToCart(AddCartRequest addCartRequest);

    void clearCart();

    CartVO listCart();

    CartItemVO deleteItem(Long productID);

    CartItemVO updateCart(UpdateCartRequest request);
}
