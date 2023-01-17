package nz.co.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponRecordVO {

    private Long id;
    @JsonProperty(value="coupon_id")
    private Long couponId;

    @JsonProperty(value="create_time")
    private Date createTime;

    @JsonProperty(value="use_state")
    private String useState;

    @JsonProperty(value="user_id")
    private Long userId;

    @JsonProperty(value="user_name")
    private String userName;

    @JsonProperty(value="coupon_title")
    private String couponTitle;

    @JsonProperty(value="start_time")
    @JsonFormat(pattern = "YYYY/MM/DD hh:mm:ss",locale = "zh",timezone = "GMT+8")
    private Date startTime;
    @JsonFormat(pattern = "YYYY/MM/DD hh:mm:ss",locale = "zh",timezone = "GMT+8")
    @JsonProperty(value="end_time")
    private Date endTime;
    @JsonProperty(value="order_id")
    private Long orderId;
    private BigDecimal price;
    @JsonProperty(value="condition_price")
    private BigDecimal conditionPrice;

}
