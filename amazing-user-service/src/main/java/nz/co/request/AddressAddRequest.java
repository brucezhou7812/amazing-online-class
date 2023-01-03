package nz.co.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value="Address Adding Request",description = "the request for adding address")
public class AddressAddRequest {
    @ApiModelProperty(value="Address id",example = "1")
    private Long id;

    @ApiModelProperty(value="user id",example = "1")
    private Long userId;

    @ApiModelProperty(value="default status 1-default address,0-common address ",example = "1")
    @JsonProperty("default_status")
    private Integer defaultStatus;

    @ApiModelProperty(value="the name of the customer",example = "Bruce")
    @JsonProperty("receive_name")
    private String receiveName;

    @ApiModelProperty(value="the phone number of the customer",example = "18911717812")
    private String phone;
    @ApiModelProperty(value="the province part of the address",example = "Beijing")
    private String province;
    @ApiModelProperty(value="the city part of the address",example = "Beijing")
    private String city;
    @ApiModelProperty(value="the region part of the address",example = "Dongcheng")
    private String region;
    @ApiModelProperty(value="the detail address",example = "Qingnianhudongli")
    @JsonProperty("detail_address")
    private String detailAddress;

}
