package nz.co.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
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
@TableName("product")
public class ProductDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * the product title to show
     */
    private String title;

    /**
     * the image of the product
     */
    private String coverImg;

    /**
     * the detail description of the product
     */
    private String detail;

    /**
     * the price of the product
     */
    private BigDecimal price;

    /**
     * creating time
     */
    private Date createTime;

    /**
     * the old price of the product
     */
    private BigDecimal oldPrice;

    /**
     * the locking stock
     */
    private Integer lockStock;

    /**
     * the stock of the product
     */
    private Integer stock;


}
