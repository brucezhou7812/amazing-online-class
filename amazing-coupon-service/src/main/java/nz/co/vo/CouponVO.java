package nz.co.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class CouponVO {
    private Long id;
    private String category;
    private String publish;
    @JsonProperty(value= "coupon_img")
    private String couponImg;
    @JsonProperty(value= "coupon_title")
    private String couponTitle;
    private BigDecimal price;
    @JsonProperty(value= "user_limit")
    private Integer userLimit;
    @JsonFormat(pattern = "YYYY/MM/DD hh:mm:ss",locale = "zh",timezone = "GMT+8")
    @JsonProperty(value= "start_time")
    private Date startTime;
    @JsonFormat(pattern = "YYYY/MM/DD hh:mm:ss",locale = "zh",timezone = "GMT+8")
    @JsonProperty(value= "end_time")
    private Date endTime;
    @JsonProperty(value= "publish_count")
    private Integer publishCount;
    private Integer stock;
    @JsonFormat(pattern = "YYYY/MM/DD hh:mm:ss",locale = "zh",timezone = "GMT+8")
    @JsonProperty(value= "create_time")
    private Date createTime;
    @JsonProperty(value="condition_price")
    private BigDecimal conditionPrice;
}
