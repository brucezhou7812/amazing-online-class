package nz.co;

import lombok.extern.slf4j.Slf4j;
import nz.co.config.PayUrlConfig;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = OrderApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class TestInjection {
    @Autowired
    private PayUrlConfig payUrlConfig;
    @Test
    public void testConfigInjection(){
        log.info("Test"+payUrlConfig.getAlipayCallbackUrl());
    }
}
