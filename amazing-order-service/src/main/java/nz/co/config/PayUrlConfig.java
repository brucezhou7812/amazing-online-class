package nz.co.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PayUrlConfig {
    @Value("${alipay.success_return_url}")
    private String alipaySuccessReturnUrl;
    @Value("${alipay.callback_url}")
    private String alipayCallbackUrl;
}
