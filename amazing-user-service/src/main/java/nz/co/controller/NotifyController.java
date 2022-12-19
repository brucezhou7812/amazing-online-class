package nz.co.controller;

import com.google.code.kaptcha.Producer;
import com.sun.javafx.iio.ios.IosImageLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/api/user/v1")
@Api(tags="Kaptcha API")
@Slf4j
public class NotifyController {
    @Autowired
    private Producer producer;
    @GetMapping("kaptcha")
    @ApiOperation("Get verification code")
    public void getKaptcha(HttpServletRequest request, HttpServletResponse repsone){
        String kaptchaText = producer.createText();
        log.info("verification code {}",kaptchaText);
        BufferedImage bufferedImage = producer.createImage(kaptchaText);
        try {
            ServletOutputStream outputStream = repsone.getOutputStream();
            ImageIO.write(bufferedImage,"jpg",outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("Can not get verification code.");
        }
    }
}
