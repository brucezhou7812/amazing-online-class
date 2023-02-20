package nz.co.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@ApiModel("The request for generating order")
@Data
public class GenerateOrderRequest {
    @JsonProperty(value="serial_no")
    @ApiModelProperty(value="serial no",example = "123456abc")
    private String serialNo;
    @JsonProperty(value="coupon_record_id")
    @ApiModelProperty(value="coupon record id",example = "1")
    private Long couponRecordId;
    @JsonProperty(value="product_ids")
    @ApiModelProperty(value="the list of product id",example = "[1,2,3]")
    private List<Long> productIds;
    @JsonProperty(value="pay_type")
    @ApiModelProperty(value="pay type",example = "ALIPAY")
    private String payType;
    @JsonProperty(value="client_type")
    @ApiModelProperty(value="client type",example = "H5")
    private String clientType;
    @JsonProperty(value="address_id")
    @ApiModelProperty(value="address id",example = "1")
    private Long addressId;
    @JsonProperty(value="total_fee")
    @ApiModelProperty(value="total fee",example = "1000")
    private BigDecimal totalFee;
    @JsonProperty(value="fee_to_pay")
    @ApiModelProperty(value="The fee that the customer should pay",example = "1000")
    private BigDecimal feeToPay;
    @JsonProperty(value="token")
    @ApiModelProperty(value="The token to prevent submit more than once",example = "12245678xxxr55")
    private String token;
}
