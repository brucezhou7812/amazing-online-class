package nz.co.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserLoginModel {
    private Long id;
    private String name;
    @JsonProperty(value="head_img")
    private String headImg;
    private String mail;
}
