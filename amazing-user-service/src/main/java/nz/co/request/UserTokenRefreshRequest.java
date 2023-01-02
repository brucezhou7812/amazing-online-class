package nz.co.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="Token Refresh Request",description = "refresh user token")
public class UserTokenRefreshRequest {
    @ApiModelProperty(value="Access Token",example = "123456")
    private String accessToken;
    @ApiModelProperty(value="Refresh Token",example = "123456")
    private String refreshToken;
    //@ApiModelProperty(value="Access Token expiretime(ms)",example = "123456")
    //private Long expire;
}
