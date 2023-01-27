package nz.co.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewUserRequest {
    private Long userId;
    private String name;
}
