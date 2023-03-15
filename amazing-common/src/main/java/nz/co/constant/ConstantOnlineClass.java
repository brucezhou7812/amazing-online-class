package nz.co.constant;

public class ConstantOnlineClass {
    public static final Long EXPIRE_TIME_FOR_VERIFICATION_CODE_TIME = 1000*60*10L;
    public static final Long EXPIRE_TIME_FOR_REFRESH_TOKEN_IN_REDIS = 1000*60*60*24*70L;
    public static final Long EXPIRE_TIME_FOR_ORDER_PAYMENT = 1000*5*60L;
    public static final Long EXPIRE_TIME_FOR_ACCESS_TOKEN = 1000*60*60*24*7L;
    public static final String KEY_IN_REDIS_REFRESH_TOKEN = "UserService:refreshToken:%s";
    public static final String KEY_IN_REDIS_VERIFICATION_CODE = "NotifyService:VerificationCode:%s:%s";
    public static final String PAGINATION_TOTAL_PAGES = "total_page";
    public static final String PAGINATION_TOTAL_RECORDS = "total_record";
    public static final String PAGINATION_CURRENT_DATA = "current_data";
    public static final String KEY_IN_REDIS_CART="ProductService:cart:%s";
    public static final String KEY_IN_REDIS_ORDER_TOKEN="ProductService:order:token:%s";
    public static final String RABBITMQ_X_DEAD_LETTER_EXCHANGE="x-dead-letter-exchange";
    public static final String RABBITMQ_X_DEAD_LETTER_ROUTING_KEY="x-dead-letter-routing-key";
    public static final String RABBITMQ_X_MESSAGE_TTL="x-message-ttl";
    public static final String OUT_TRADE_NO="out_trade_no";
    public static final String TRADE_SATATUS = "trade_status";
    public static final String TRADE_SUCCESS="TRADE_SUCCESS";
    public static final String TRADE_FINISHED="TRADE_FINISHED";
    public static final String TOKEN="token";
}
