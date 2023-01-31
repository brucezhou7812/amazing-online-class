package nz.co.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="New User Request",description = "The request to create a new user")
public class NewUserRequest {
    @ApiModelProperty(value="User Id",example = "123")
    private Long userId;
    @ApiModelProperty(value="User Name",example = "Bruce")
    private String name;
}
