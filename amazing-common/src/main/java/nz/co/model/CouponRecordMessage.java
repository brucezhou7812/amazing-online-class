package nz.co.model;

import lombok.Data;

@Data
public class CouponRecordMessage {
    private Long messageId;
    private String serialNum;
    private Long couponTaskId;
}
