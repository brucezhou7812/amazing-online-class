package nz.co.constant;

public class ConstantOnlineClass {
    public static final Long EXPIRE_TIME_FOR_VERIFICATION_CODE_TIME = 1000*60*10L;
    public static final Long EXPIRE_TIME_FOR_REFRESH_TOKEN_IN_REDIS = 1000*60*60*24*70L;
    public static final Long EXPIRE_TIME_FOR_ACCESS_TOKEN = 1000*60*60*24*7L;
    public static final String KEY_IN_REDIS_REFRESH_TOKEN = "UserService:refreshToken:%s";
    public static final String KEY_IN_REDIS_VERIFICATION_CODE = "NotifyService:VerificationCode:%s:%s";
    public static final String PAGINATION_TOTAL_PAGES = "total_page";
    public static final String PAGINATION_TOTAL_RECORDS = "total_record";
    public static final String PAGINATION_CURRENT_DATA = "current_data";
}
