package nz.co.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@TableName("banner")
public class BannerDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * the id of the banner
     */
      @TableId(value = "id", type = IdType.AUTO)
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
