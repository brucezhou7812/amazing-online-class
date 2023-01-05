package nz.co.enums;

import lombok.Data;
import lombok.Getter;


public enum CouponCategoryEnum {
    COUPON_CATEGORY_PROMOTION(0,"PROMOTION"),
    COUPON_CATEGORY_NEW_USER(1,"NEW_USER"),
    COUPON_CATEGORY_TASK(2,"TASK");
    @Getter
    private int code;
    @Getter
    private String desc;
    private CouponCategoryEnum(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

}
