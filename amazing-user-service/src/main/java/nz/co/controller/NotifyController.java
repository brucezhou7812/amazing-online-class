package nz.co.controller;

import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nz.co.enums.BizCodeEnum;
import nz.co.enums.SendCodeEnum;
import nz.co.service.NotifyService;
import nz.co.utils.CommonUtils;
import nz.co.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/notify/v1")
@Api(tags="Notify Service Module")
@Slf4j
public class NotifyController {
    @Autowired
    private Producer producer;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private NotifyService notifyService;

    private final long expireTimeForKaptcha = 60*10*1000;
    @GetMapping("kaptcha")
    @ApiOperation("Get verification code")
    public void getKaptcha(HttpServletRequest request, HttpServletResponse repsone){
        String kaptchaText = producer.createText();
        log.info("verification code {}",kaptchaText);
        redisTemplate.opsForValue().set(getKaptchaKey(request),kaptchaText,expireTimeForKaptcha, TimeUnit.MILLISECONDS);
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
    @ApiOperation("send verification code through email")
    @GetMapping(value="send_code")
    public JsonData sendMailVerificationCode(@ApiParam(value="to",required = true) @RequestParam(value="to",required = true)String to,
                                             @ApiParam(value="kaptcha",required = true)@RequestParam(value="kaptcha",required = true)String kaptcha,
                                             HttpServletRequest request){
        String cacheKaptcha = redisTemplate.opsForValue().get(getKaptchaKey(request));
        if(cacheKaptcha != null && cacheKaptcha.equalsIgnoreCase(kaptcha)){
            redisTemplate.delete(cacheKaptcha);
            return notifyService.sendCode(SendCodeEnum.REGISTER_CODE,to);
        }else{
            return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
        }
    }

    private String getKaptchaKey(HttpServletRequest request){
        String ip = CommonUtils.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        String kapchaKey = "User-Service:kaptcha:"+CommonUtils.MD5(ip+userAgent);
        log.info("Kaptcha key is {}"+kapchaKey);
        return kapchaKey;
    }
}
