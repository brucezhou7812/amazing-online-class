package nz.co.enums;

import lombok.Getter;

public enum CouponUseStateEnum {
    COUPON_NEW(0,"NEW"),
    COUPON_EXPIRED(1,"EXPIRED"),
    COUPON_USED(2,"USED");
    @Getter
    private int code;
    @Getter
    private String desc;
    private CouponUseStateEnum(int code,String desc){
        this.code = code;
        this.desc = desc;
    }
}
