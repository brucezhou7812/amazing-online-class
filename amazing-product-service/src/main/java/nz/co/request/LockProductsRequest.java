package nz.co.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="Lock Products Stock Request")
public class LockProductsRequest {
    @ApiModelProperty(value="serial_no",example="123456abc")
    @JsonProperty(value="serial_no")
    private String serialNo;
    @ApiModelProperty(value="orderItems",example="[]")
    @JsonProperty(value="order_items")
    private List<OrderItemRequest> orderItems;
}
