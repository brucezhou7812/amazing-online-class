package nz.co.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddressVO {
    private Long id;
    @JsonProperty(value = "user_id")
    private Long userId;
    @JsonProperty(value="default_status")
    private Integer defaultStatus;
    @JsonProperty(value="receive_name")
    private String receiveName;
    private String phone;
    private String province;
    private String city;
    private String region;
    @JsonProperty(value="detail_address")
    private String detailAddress;


}
