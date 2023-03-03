package nz.co.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.boot.autoconfigure.web.ServerProperties;

public class AlipayConfig {
    public static final String FORMAT = "json";

    public static final  String APPID="2021003179686005";

    public static final String PAY_GATEWAY="https://openapi.alipay.com/gateway.do";

    public static final String APP_PRI_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCwIyuuQ21doCjs03a+hcZtaxtti3sV26afC+dpV0RPUEKmQFh1GARXP8kV0NYZnYHDbfQW0vB3B9ZK9cRhJEx4Dl2o1RkJJ6cdOBKebWzT40fL9xOa6CytLPyMWIHzjI1phm1aqFeNEnT0kz0hd1C8aOlB7hKL7KcQctoYOxz0+lzS4u/dyDwJ2iLDshQNftB8hLEKx0ZaCHMNJcBOGjRKPa3X16NvKiaivscqwD9vWL82pdxf1MmqxBL3d3lxRiFI9iF85zMTAw8wyt9W0HqEH/a9Bcbu6KYOhXuMPH4Eqgl/hX7v2a0YTkPzi9wFEJ4l4t+7DoULcHwCi/NY+XCHAgMBAAECggEAAvQkCGETFW3ZIdeN9zXxFOal6BldsAkl6ybc85RGDfpvk7Aii5wifoAQVDBTVsygX3GZlURmSei0vD8Q0vwz2BONmIYyTEgA8ml9TnDRUWBPVOlIx+E61pCZj5TwYPArcuiRLuMBzNjRY0SQ0t3YWcB8oOFoKDaIuqriY2OiYa1F/jo/Wt51lgbE9UPzrDKDaicxaam9VM76+HE3vkD0H6Ez+A5wf+eZinHZzhWwXHglc+Q4Pw9w6KJi7WylqWyWUzRJTaS1v0C9tWEdZBCj/8Ft9OqVYZfr97GhTYtX/2OTje3DluUcMMulh2fXvFj3k4hCU8GQw2g1fdEDzVTXOQKBgQDd+RkZVUb9XyiXZLIFrekRoYD7h/GgfEc2lNvkuOuF2CzBoLdOksDJaMwCNh5ZN7rpYou9FL4VQrGTVyEn34OhkroQzzHQJ/keSKtd/oJJNCcVbhK57aJtyUwFeUsXA666XfEc78g5w+22Kfb/lIFU9g7DuU+UjD54RWuPMeSG5QKBgQDLI1dUSw1eO7tU2vHcfVuZ5Vrt6nT6wuTtZ7S7yw/YeL7J6bIFlOBK0FXxdkOwI3cvgFH+2Xy9PZvNPTRKGUWztLHQZ1udKVpQYjlqPfnakRhk1qCFZKsvRnud3a6n0rndzhz/UETo/F03lns0ckJvCiF6V/VTgJl2kmF4VnOW+wKBgGXDFjOb+dCcuII3grnB0fDoRraJy0dU0lz+f5TrBea1hUc7g+38r29moOh+oQkr2H+4UvHbOY2LptcygZvDB5iiKLUeF+si+D1e+Vk9HcOrqiNT9DfS7r1AJJ75zWf+ozMDcE9gyj1ZSnhR4s+m6Gfn6jrmkMo8RUNkJ/xc1jTVAoGAQBvgNLcW+fVp8qbCi+54GUKcBtfmoTke3JnNHTNBxfSdacW6LC7G9nV3miPTyuP2emRJ/F8REfw+jnBRb004hTVypK3p6NijKxlEu3Xhw4vlIfXmZRLXP61D2BedRlOn2wtwdVcU2F7E4IU6Ttv8gWS2GhFmWDsOs7AQ2NSX2D8CgYEAs+JScTpjE+KsJIiDU+7f08DlutES/JWJ3IL5oT/YJBYaIx36MDZKO395xdHsjhrd9AwmQoXpgkPWYVTF+xAV289VBDefawL2MJiUhpMvts7LtRGeQOb/aj9+f8wwoeOg+buzuDoWzap7E1pNIA8ZpVHBHLYOY2Lc6kjNjGooBoA=";
    public static final String ALIPAY_PUB_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtN3xhxEUcjo3xH66cLEJAo+M6KYUkwx2GJNrACu7VjS1dMsb7knztBv4oJDFDyKDOHbPGE/IUoqhGzqZpJG+ug1rM0x0tG+4tHCFS/8ZX+x8vvrTGX4YMCM2fQh9I0r01fnhPsh8XQ3J4BX6vQUpqAmji+SW3ciWd2ln7CHQn0HCK1og/Z1KrEVOGnpyWN6vWllJQW6ecav7FqbZTFVJIM9F133bnVk1+VmC1rM6xByUH97+aVfK0af+xbApXn2GjSR1sgli3Lqmrfh/z7ExKnje02gRFByLeluDAysu60Sq0NDtclPgA5nQOn+FCrCNsJjaNMQJ0zM2J7t+9SrffQIDAQAB";

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
