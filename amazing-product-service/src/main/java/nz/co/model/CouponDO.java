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
 * @since 2023-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coupon")
public class CouponDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * coupon type[NEW_USER，TASK，PROMOTION]
     */
    private String category;

    /**
     * pubish type, PUBLISH，DRAFT，OFFLINE
     */
    private String publish;

    /**
     * coupon imgage
     */
    private String couponImg;

    /**
     * coupon title
     */
    private String couponTitle;

    /**
     * coupon price
     */
    private BigDecimal price;

    /**
     * quantity limit
     */
    private Integer userLimit;

    /**
     * start time
     */
    private Date startTime;

    /**
     * end time
     */
    private Date endTime;

    /**
     * the total count of the coupon
     */
    private Integer publishCount;

    /**
     * the stock of the coupon
     */
    private Integer stock;

    private Date createTime;

    /**
     * only when the condition is met the coupon is valid, for example,the total cost is over a specifc number
     */
    private BigDecimal conditionPrice;


}
