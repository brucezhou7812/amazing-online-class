package nz.co.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


public class CartItemVO {
    @JsonProperty(value="product_id")
    private Long productId;
    @JsonProperty(value="buy_num")
    private Integer buyNum;
    @JsonProperty(value="price")
    private BigDecimal price;
    @JsonProperty(value="total_fee")
    private BigDecimal totalFee;
    @JsonProperty(value="product_title")
    private String productTitle;
    @JsonProperty(value="product_img")
    private String productImg;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(Integer buyNum) {
        this.buyNum = buyNum;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalFee() {
        totalFee = this.price.multiply(new BigDecimal(this.buyNum));
        return totalFee;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

}
