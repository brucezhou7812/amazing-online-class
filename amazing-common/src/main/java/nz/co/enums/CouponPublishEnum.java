package nz.co.enums;

import lombok.Getter;

public enum CouponPublishEnum {
    COUPON_PUBLISH(0,"PUBLISH"),
    COUPON_OFFLINE(1,"OFFLIN"),
    COUPON_DRAFT(2,"DRAFT");
    @Getter
    private int code;
    @Getter
    private String desc;
    private CouponPublishEnum(int code,String desc){
        this.code = code;
        this.desc = desc;
    }
}
