package nz.co.service;

import nz.co.request.AddCartRequest;

public interface CartService {
    void addToCart(AddCartRequest addCartRequest);
}
