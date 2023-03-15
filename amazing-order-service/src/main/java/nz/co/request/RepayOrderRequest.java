package nz.co.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RepayOrderRequest {
    @JsonProperty(value = "serial_no")
    private String serialNo;
    @JsonProperty(value="pay_type")
    private String payType;
    @JsonProperty(value="client_type")
    private String clientType;
}
