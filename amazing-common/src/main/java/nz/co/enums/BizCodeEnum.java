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
    ACCOUNT_PWD_ERROR(250003,"Error Password");
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
