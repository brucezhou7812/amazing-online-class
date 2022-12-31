package nz.co.biz;

import lombok.extern.slf4j.Slf4j;
import nz.co.UserApplication;
import nz.co.component.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = UserApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class MailServiceTest {
    @Autowired
    private MailService mailService;
    @Test
    public void sendMailTest(){
        mailService.sendMail("18911717812@189.cn","test","Hello,This is a test mail");
    }
}
