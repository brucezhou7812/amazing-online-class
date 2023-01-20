package nz.co.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BannerVO{

    /**
     * the id of the banner
     */
    @JsonProperty(value="id")
    private Integer id;

    /**
     * the image of the banner
     */
    @JsonProperty(value="img")
    private String img;

    /**
     * the url of the image
     */
    @JsonProperty(value="url")
    private String url;

    /**
     * the weight of the image
     */
    @JsonProperty(value="weight")
    private Integer weight;


}
