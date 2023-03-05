package nz.co.enums;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Enum:Error code and Error Message
 */

public enum BizCodeEnum {
    /**
     * General Error code
     */
    OPS_REPEAT(110001,"Repetitive Operation"),
    /**
     * verification code error
     */
    CODE_TO_ERROR(240001,"Phone number invalid"),
    CODE_LIMITED(240002,"Frequency too fast"),
    CODE_ERROR(240003,"Verification code error"),
    CODE_CAPTCHA(240004,"CAPTCHA error"),
    /**
     * account error
     */
    ACCOUNT_REPEAT(250001,"Repetitive Account"),
    ACCOUNT_UNREGISTER(250002,"Account Unregister"),
    ACCOUNT_PWD_ERROR(250003,"Error Password"),
    ACCOUNT_NOT_LOGIN(250004,"Please Login"),

    /**
     * address error
     */
    ADDRESS_NOT_EXIST(260001,"Address not exist"),

    COUPON_NOT_EXIST(270001,"Coupon not exist"),
    COUPON_EXCEED_USER_LIMIT(270002,"Coupon exceeds user limit"),
    COUPON_LOCK_FAIL(270003,"Coupon locking failed"),
    COUPON_TASK_NOT_EXIST(280001,"Coupon Task Record not exist"),
    COUPON_RECORD_NOT_EXIST(290001,"Coupon Record Not Exist"),
    COUPON_RECORD_CONFIRM_FAILED(2990002,"Coupon Record confirm failed"),
    COUPON_RECORD_INVALIDE(290003,"Coupon Record Invalid"),
    COUPON_RECORD_DO_NOT_MEET_PRICE_CONDITION(290004,"Coupon does not meet price condition"),
    /**
     * File error
     */
    FILE_UPLOAD_USER_IMAGE_FAIL(610001,"Upload user image fail"),

    /**
     * Banner error
     */
    BANNER_NOT_EXIST(710001,"Banner not exist"),
    /**
     * product error
     */
    PRODUCT_NOT_EXIST(810001,"Product not exist"),

    /**
     * cart error
     */
    CART_NOT_EXIST(820001,"Cart not exist"),
    CART_IS_EMPTY(820002,"Cart is empty"),
    /**
     * Order error
     */
    ORDER_NOT_EXIST(830001,"Order not exist"),
    ORDER_LOCK_STOCK_FAILED(830002,"Lock stock failed"),
    ORDER_CONFIRM_FAILED(830003,"Confirm order failed"),
    ORDER_TIMEOUT(830004,"Order has been expired"),
    ORDER_PAID_FAILED(830006,"Order Paid Failed"),
    ORDER_CALLBACK_FAILED(830005,"Order callback failed");

    @Getter
    @Setter
    private int code;
    @Getter
    @Setter
    private String message;
    private BizCodeEnum(int code,String message){
        this.code = code;
        this.message = message;
    }
}
