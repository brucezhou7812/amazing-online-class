package nz.co.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class CouponTaskVO {

    private Long id;

    /**
     * coupon id
     */
    private Long couponRecordId;

    /**
     * create time
     */
    private Date createTime;

    /**
     * the serial number of the order
     */
    private String serialNum;

    /**
     * LOCKED,FINISHED,CANCELLED
     */
    private String lockState;

}
