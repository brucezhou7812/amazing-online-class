package nz.co.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("Lock coupon record request")
public class LockCouponRecordRequest {
    @ApiModelProperty(value="the serial number of goods",example = "sdfddss111")
    @JsonProperty("serial_num")
    private String serialNum;
    @ApiModelProperty(value="the list of coupon id",example = "[1,2,3]")
    @JsonProperty("coupon_ids")
    private List<Long> couponIds;
}
