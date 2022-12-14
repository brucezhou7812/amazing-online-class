package nz.co.service.impl;

import com.mysql.cj.util.StringUtils;
import nz.co.constant.ConstantOnlineClass;
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

    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {
        String code = CommonUtils.getRandomCode(6);
        String cacheCodeKey = String.format(ConstantOnlineClass.KEY_IN_REDIS_VERIFICATION_CODE,SendCodeEnum.REGISTER_CODE.name(),to);
        String cacheCode = redisTemplate.opsForValue().get(cacheCodeKey);
        String codeWithTimestamp = code+"_"+CommonUtils.getTimestamp();
        if(!StringUtils.isNullOrEmpty(cacheCode)){
            String lastTimestamp = cacheCode.split("_")[1];
            long ttl = CommonUtils.getTimestamp() - Long.parseLong(lastTimestamp);
            if(ttl < ConstantOnlineClass.EXPIRE_TIME_FOR_VERIFICATION_CODE_TIME)
                return JsonData.buildResult(BizCodeEnum.CODE_LIMITED);
        }
        redisTemplate.opsForValue().set(cacheCodeKey,codeWithTimestamp,ConstantOnlineClass.EXPIRE_TIME_FOR_VERIFICATION_CODE_TIME,TimeUnit.MILLISECONDS);
        if(CommonUtils.isEmail(to)) {

            String content = String.format(CONTEXT, code);
            mailService.sendMail(to, SUBJECT, content);
            return JsonData.buildSuccess(content);
        }else if(CommonUtils.isPhone(to)){

        }
        return JsonData.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }

    private boolean checkExpired(String cacheCode,String code){
        String ttlString = cacheCode.split("_")[1];
        long ttl = Long.parseLong(ttlString);
        long currentTimestamp = CommonUtils.getTimestamp();
        return (currentTimestamp-ttl)>ConstantOnlineClass.EXPIRE_TIME_FOR_VERIFICATION_CODE_TIME ?true:false;

    }
    private boolean checkCodeEqual(String cacheCode,String code){
        String cacheCodePrefix = cacheCode.split("_")[0];
        return cacheCodePrefix.equals(code);
    }
    @Override
    public boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code) {
        String cacheCodeKey = String.format(ConstantOnlineClass.KEY_IN_REDIS_VERIFICATION_CODE,SendCodeEnum.REGISTER_CODE.name(),to);
        String cacheCode = redisTemplate.opsForValue().get(cacheCodeKey);
        if(!StringUtils.isNullOrEmpty(cacheCode)){
            redisTemplate.delete(cacheCodeKey);
            if(!checkCodeEqual(cacheCode,code)){
                //incoming verification code does not equal to the cached one
                return false;
            }else if(checkExpired(cacheCode,code)){
                //verification code is expired
                return false;
            }
            return true;
        }else{
            return false;
        }
    }
}
