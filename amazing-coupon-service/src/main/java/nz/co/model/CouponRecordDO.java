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
@TableName("coupon_record")
public class CouponRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * coupon id
     */
    private Long couponId;

    /**
     * create time
     */
    private Date createTime;

    /**
     * use state  [NEW,USED,EXPIRED]
     */
    private String useState;

    /**
     * user id
     */
    private Long userId;

    /**
     * user name
     */
    private String userName;

    /**
     * coupon titiel
     */
    private String couponTitle;

    /**
     * start time
     */
    private Date startTime;

    /**
     * end time
     */
    private Date endTime;

    /**
     * order id
     */
    private Long orderId;

    /**
     * price
     */
    private BigDecimal price;

    /**
     * condition
     */
    private BigDecimal conditionPrice;


}
