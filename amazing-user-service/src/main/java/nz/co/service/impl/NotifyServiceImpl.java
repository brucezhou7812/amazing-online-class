package nz.co.service.impl;

import com.mysql.cj.util.StringUtils;
import nz.co.enums.BizCodeEnum;
import nz.co.enums.SendCodeEnum;
import nz.co.component.MailService;
import nz.co.service.NotifyService;
import nz.co.utils.CommonUtils;
import nz.co.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class NotifyServiceImpl implements NotifyService {
    @Autowired
    private MailService mailService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private final String SUBJECT = "Amazing online class verification code";
    private final String CONTEXT = "Amazing online class verification code is %s. It is valid in 60 seconds,please do not leak to others!";
    private final String CACHECODEKEY = "NotifyService:VerificationCode:%s:%s";
    private final long EXPIREDTIME = 60*1000*10;
    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {
        String code = CommonUtils.getRandomCode(6);
        String cacheCodeKey = String.format(CACHECODEKEY,SendCodeEnum.REGISTER_CODE.name(),to);
        String cacheCode = redisTemplate.opsForValue().get(cacheCodeKey);
        String codeWithTimestamp = code+"_"+CommonUtils.getTimestamp();
        if(!StringUtils.isNullOrEmpty(cacheCode)){
            String lastTimestamp = cacheCode.split("_")[1];
            long ttl = CommonUtils.getTimestamp() - Long.parseLong(lastTimestamp);
            if(ttl < EXPIREDTIME)
                return JsonData.buildResult(BizCodeEnum.CODE_LIMITED);
        }
        redisTemplate.opsForValue().set(cacheCodeKey,codeWithTimestamp,EXPIREDTIME,TimeUnit.MILLISECONDS);
        if(CommonUtils.isEmail(to)) {

            String content = String.format(CONTEXT, code);
            mailService.sendMail(to, SUBJECT, content);
            return JsonData.buildSuccess(content);
        }else if(CommonUtils.isPhone(to)){

        }
        return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }
}
