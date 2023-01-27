package nz.co.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CartRequest {
    @ApiModelProperty(value="product id",example = "1")
    @JsonProperty(value="product_id")
    protected Long productId;
    @ApiModelProperty(value="the number of products to be bought",example = "2")
    @JsonProperty(value="buy_num")
    protected Integer buyNum;
}
