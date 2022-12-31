package nz.co.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOSSConifg {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySercret;
    private String bucketname;
}
