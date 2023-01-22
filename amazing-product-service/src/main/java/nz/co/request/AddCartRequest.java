package nz.co.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="The request for adding product to the cart")
public class AddCartRequest {
    @ApiModelProperty(value="product id",example = "1")
    @JsonProperty(value="product_id")
    private Long productId;
    @ApiModelProperty(value="the number of products to be bought",example = "2")
    @JsonProperty(value="buy_num")
    private Integer buyNum;
}
