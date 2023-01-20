package nz.co.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class CartVO {
    @JsonProperty(value="cart_items")
    private List<CartItemVO> cartItems;
    @JsonProperty(value="user_id")
    private Long userId;
    @JsonProperty(value="total_fee")
    private BigDecimal totalFee;
    @JsonProperty(value="fee_to_pay")
    private BigDecimal feeToPay;
    @JsonProperty(value="buy_num")
    private Integer buyNum;

    public Integer getBuyNum() {
        this.buyNum = 0;
        if(cartItems!=null) {
            this.buyNum = this.cartItems.stream().mapToInt(CartItemVO::getBuyNum).sum();
        }
        return buyNum;
    }

    public List<CartItemVO> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemVO> cartItems) {
        this.cartItems = cartItems;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalFee() {
        totalFee = new BigDecimal(0);
        if(cartItems!=null) {
            for (CartItemVO cartItem : cartItems) {
                totalFee = totalFee.add(cartItem.getTotalFee());
            }
        }
        return totalFee;
    }

    public BigDecimal getFeeToPay() {
        return feeToPay;
    }

    public void setFeeToPay(BigDecimal feeToPay) {
        this.feeToPay = feeToPay;
    }
}
