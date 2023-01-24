package nz.co.service;

import nz.co.request.AddCartRequest;
import nz.co.vo.CartVO;

import java.util.List;

public interface CartService {
    void addToCart(AddCartRequest addCartRequest);

    void clearCart();

    CartVO listCart();
}
