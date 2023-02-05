package nz.co.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="Order Item Request")
public class OrderItemRequest {
    @ApiModelProperty(value="product id",example = "12")
    @JsonProperty(value="product_id")
    private Long productId;
    @ApiModelProperty(value="the amount of products to be sold",example = "12")
    @JsonProperty(value="buy_num")
    private Integer buyNum;
}
