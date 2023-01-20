package nz.co.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private Integer id;

    /**
     * the image of the banner
     */
    private String img;

    /**
     * the url of the image
     */
    private String url;

    /**
     * the weight of the image
     */
    private Integer weight;


}
