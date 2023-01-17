package nz.co.config;

import lombok.Data;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class RedisLockConfig {
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.password}")
    private String redisPwd;
    @Value("${spring.redis.port}")
    private String redisPort;
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+redisHost+":"+redisPort)
                .setPassword(redisPwd);
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
