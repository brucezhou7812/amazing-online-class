package nz.co.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="User Register Entity",description = "This entity is used to carry user properties when registering")
public class UserRegisterRequest {
    @ApiModelProperty(value="User name",example = "Bruce")
    private String name;
    @ApiModelProperty(value="Password",example = "123456")
    private String pwd;
    @ApiModelProperty(value="Head image",example="http://xxx.com/xxx.jpg")
    private String headImg;
    @ApiModelProperty(value="User signature",example = "no pain no gain")
    private String slogan;
    @ApiModelProperty(value="Sex:0-female,1-male",example = "1")
    private Integer sex;
    @ApiModelProperty(value="User email",example = "18911717812@189.cn")
    private String mail;
    @ApiModelProperty(value="Verification code",example = "123456")
    private String code;

}
