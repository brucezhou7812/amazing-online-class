package nz.co.biz;

import lombok.extern.slf4j.Slf4j;
import nz.co.UserApplication;
import nz.co.service.AddressService;
import nz.co.model.AddressDO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = UserApplication.class)
@Slf4j
@RunWith(SpringRunner.class)
public class AddressTest {
    @Autowired
    private AddressService addressService;
    @Test
    public void testAddressDetail(){
        AddressDO addressDo = addressService.detail(1L);
        log.info("print addressDO");
        log.info(addressDo.toString());
        Assert.assertNotEquals(addressDo,null);
    }
}
