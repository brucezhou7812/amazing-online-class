package nz.co.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.boot.autoconfigure.web.ServerProperties;

public class AlipayConfig {
    public static final String FORMAT = "json";

    public static final  String APPID="2021000122616063";

    public static final String PAY_GATEWAY="https://openapi.alipaydev.com/gateway.do";

    public static final String APP_PRI_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCXq6I91WRG9S+40g5NwVoUKpo46TDc64/m9r0J9L0141U792V1Wi9O+mNVCQOD8KddWOZJ/nZBlHuQExRMg05ukoWKPhDkU2BxKWLonguyEBArleyJLqglXVtDzJBR8q7QD2+JGXNmnWAOh57wAaBO8sh9PJox3YGLrTdHfeQyzqeuQr/2XZhTDdkF4WK3w5LT8lE7PFwFxgIYqgOpX5I49EQwryE1OWo/DQlFXsYpdFZEbmUHdqVwBpIvsPUuMElIrbUW5lCwfgY7y5tlAht75K7/WkZIqF80eeoHTp+w/yMopPzwQcQaKD/qSgGCgGb7Hk9RP2HR7TZuCjoxCUNAgMBAAECggEAcH6Q7qE2FbPFPT5T6QMASIYqtb2RRBKDDmJHah0fHKUPUHZ2Zz8yVO/o1UoAnp2c2lAcNGFZZ5rbgdG7yOh+nwyPt1V8/JjaH5r3GmmEeSHUx7to8tBctgqhvAy72bbAShnh7TvtGKAqug3sle4Z7tQQsWly71UGE8mjyZV9NVPRWCBi+chqaLWI3c0OJGVbMp7KHM1WwyvIiQ2MpXoIRBy7hwTOQKZ9tTeJFZ9VMSb8iO6I8YPqLBRg+2/+R8htkolhqlXsQrFCSSVK6rQvNxKjMfpgDSFHhuTlT1nus02W4jeyQQvSra28VfnJ4xYnseyWg8JxmiWdVTC6DLlihQKBgQDv/NX/9jAvZoJbvnT28YIHXnNYbxYqI8VRhjSaj22qARDQD+zXSjQlmP0DzBsxi+L+vfcsQruQwyeYvBwfpKw2OJ7wrH7ecwjOS4QYG+VaTfw+WhjM8Mw8UcPqV/tM546STytGWeOQiDdbDu9Jsn6AhlP+32RkvknmqstUUW0JzwKBgQCLEX51sekId6BxhGPPYsai+xEH4+yzHW1ud0BpHdLUvLQ1ifZlsgs5vMvLMrXDwD/Aqotr5UqkLejbp0IPvjZySxKUDIM8KO/UgB1UHHDwNi/vMvuaF00ifan1PGYTW39pIJrARKxaIMdEW3ONjbzl6U7L4FMFdHSSFdGYeNmGYwKBgQCQG+l9jJ4euJWq9dFSwG9ucV8eSYarw1Mnw0kvTtVxcrZf2lBEb4Ck2xuD/jqOJAwpIHWelGwBX88VPlA9CCuJW7LFny+H8Nfm2R33RDkTYyUHZB7kYeyihA77V93Frn7EiNnZovytu75R5cP3I0Ind4JU2Fx2ynWiGAmFFmg9rQKBgFjwSjkJG+ivYOvYaGDEYhbvjFfIe6OdiVPhbk8LpL8rI/Etl2g4cpdtV8RPXRGgzOTx7RKG5LKVLdv0XvpkgrubqXkHe5Ko96Cmgps9STlG3mcS6vgEOO97AIv3Iaz1kklwcafPA47scaQU/JSl9g3vpNYcU9wkOz0GOTJS74LTAoGACws9PwaZrSq52xufRey2sbWlcQWnnpyA1e5dXB9x4yFy1AJKGU4DqoiOTsoFE5UC52225mTKo2bsXgV8LlzrUV331x9w2WtsjXhaExKgb2RVy3R7LAxTZNOHxflO63efSOSB0Jv1VH5ewIARaTXymT9v4vuZT1j25LTGVt2Vu6c=";
    public static final String ALIPAY_PUB_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuN36/l88HBSQaM2PcdkihdmjMj9RIHFBJPppLBvXcl8r8m5MSFzVRxFyLEgELXqayQUkLArQ/T9AX7WUo9cJcDZzcO90i9v0BQY4w800KN/e3y1vJPXOC2eJefJzbGUXhBil8xCR17iWw2M9YtaTyq+m/iKbwx3LTLE9m3YfYMDbu7FLeavHEMHZz8yiFIt8ObHK8KcUiGsOp2pqYvf5+U0Qml45PoJfhbxfrm8ps0BNXmXg3ClKeyh51bq8a5du9/AaLecq3RHUTm3ELn1n8FSE5oqRVR3ukyhnT1it1m1qvB/w7Wi7qs8DWBeZcUqckE+vvWbilR4QV2zm4hVa4QIDAQAB";
    public static final  String SIGN_TYPE="RSA2";

    public static final  String CHARSET="UTF-8";

    private volatile static AlipayClient instance = null;

    private AlipayConfig(){

    }
    public static AlipayClient getInstance(){
        if(instance == null){
            synchronized (AlipayConfig.class){
                if(instance == null){
                    instance = new DefaultAlipayClient(PAY_GATEWAY,APPID,APP_PRI_KEY,FORMAT,CHARSET,ALIPAY_PUB_KEY,SIGN_TYPE);
                }
            }
        }
        return instance;
    }

}
