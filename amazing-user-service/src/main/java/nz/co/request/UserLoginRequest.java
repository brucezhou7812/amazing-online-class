package nz.co.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value="User Login Request",description = "The request carrying User Login information")
@Data
public class UserLoginRequest {
    @ApiModelProperty(value="mail",example="18911717812@189.cn")
    private String mail;
    @ApiModelProperty(value="password",example = "123456")
    private String pwd;
}
