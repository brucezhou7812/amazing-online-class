package nz.co.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import nz.co.constant.ConstantOnlineClass;
import nz.co.enums.BizCodeEnum;
import nz.co.enums.SendCodeEnum;
import nz.co.interceptor.LoginInterceptor;
import nz.co.model.UserDO;
import nz.co.mapper.UserMapper;
import nz.co.model.UserLoginModel;
import nz.co.request.UserLoginRequest;
import nz.co.request.UserRegisterRequest;
import nz.co.request.UserTokenRefreshRequest;
import nz.co.service.NotifyService;
import nz.co.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nz.co.utils.CommonUtils;
import nz.co.utils.JWTUtils;
import nz.co.utils.JsonData;
import nz.co.vo.UserVO;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2022-12-04
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private NotifyService notifyService;
    @Override
    public JsonData register(UserRegisterRequest userRegisterRequest) {
        String email = userRegisterRequest.getMail();
        String code = userRegisterRequest.getCode();
        if(notifyService.checkCode(SendCodeEnum.REGISTER_CODE,email,code)){
            UserDO userDO = new UserDO();
            BeanUtils.copyProperties(userRegisterRequest,userDO);
            userDO.setCreateTime(new Date());
            userDO.setSlogan("no pain,no gain");
            //userDO.setPwd();
            String salt = "$1$"+ CommonUtils.getRadomSalt(8);
            userDO.setSecret(salt);
            String md5Crypt =  Md5Crypt.md5Crypt(userRegisterRequest.getPwd().getBytes(),salt);
            userDO.setPwd(md5Crypt);
            //check whether the account is unique
            if(!checkMailUnique(userDO.getMail())) {
                int rows = userMapper.insert(userDO);
                log.info("insert user {} " + rows);
                userRegisterInitTask(userDO);
                return JsonData.buildSuccess();
            }else{
                return JsonData.buildResult(BizCodeEnum.ACCOUNT_REPEAT);
            }
        }
        return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
    }

    @Override
    public JsonData login(UserLoginRequest userLoginRequest) {
        QueryWrapper<UserDO> queryWrapper = new QueryWrapper<UserDO>().eq("mail",userLoginRequest.getMail());
        List<UserDO> userDOList = userMapper.selectList(queryWrapper);
        if(userDOList != null && userDOList.size() == 1){
            UserDO user = userDOList.get(0);
            String cryptPWD = Md5Crypt.md5Crypt(userLoginRequest.getPwd().getBytes(),user.getSecret());
            if(cryptPWD.equals(user.getPwd())){
                UserLoginModel userLoginModel = new UserLoginModel();
                BeanUtils.copyProperties(user,userLoginModel);
                String accesstoken = JWTUtils.generateToken(userLoginModel);
                String refreshtoken = CommonUtils.generateUUID();
                redisTemplate.opsForValue().set(refreshtoken,"1", ConstantOnlineClass.EXPIRE_TIME_FOR_REFRESH_TOKEN_IN_REDIS, TimeUnit.MILLISECONDS);
                TokenClass tokenClass = new TokenClass();
                tokenClass.setAccessToken(accesstoken);
                tokenClass.setRefreshToken(refreshtoken);
                //tokenClass.setExpire(ConstantOnlineClass.EXPIRE_TIME_FOR_VERIFICATION_CODE_TIME);
                return JsonData.buildSuccess(tokenClass);
            }else{
                return JsonData.buildResult(BizCodeEnum.ACCOUNT_PWD_ERROR);
            }
        }else{
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_PWD_ERROR);
        }

    }

    @Override
    public JsonData refresh(UserTokenRefreshRequest userTokenRefreshRequest) {
        String accessToken = userTokenRefreshRequest.getAccessToken();
        String refreshToken = userTokenRefreshRequest.getRefreshToken();
        String value = redisTemplate.opsForValue().get(refreshToken);
        if(value!=null){
            redisTemplate.delete(refreshToken);
            String newAccessToken = JWTUtils.refreshToken(accessToken);
            String newRefreshToken = CommonUtils.generateUUID();
            redisTemplate.opsForValue().set(newRefreshToken,"1",ConstantOnlineClass.EXPIRE_TIME_FOR_REFRESH_TOKEN_IN_REDIS,TimeUnit.MILLISECONDS);
            TokenClass tokenClass = new TokenClass();
            tokenClass.setAccessToken(newAccessToken);
            tokenClass.setRefreshToken(newRefreshToken);
            //tokenClass.setExpire(ConstantOnlineClass.EXPIRE_TIME_FOR_VERIFICATION_CODE_TIME);
            return JsonData.buildSuccess(tokenClass);
        }else{
            return JsonData.buildError("Refresh token not exist.");
        }

    }

    @Override
    public UserVO findUserDetail() {

        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        QueryWrapper<UserDO> queryWrapper = new QueryWrapper<UserDO>().eq("id",userLoginModel.getId());
        UserDO userDO = userMapper.selectOne(queryWrapper);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userDO,userVO);

        return userVO;
    }

    private void userRegisterInitTask(UserDO userDO){

    }


    private boolean checkMailUnique(String email){
        QueryWrapper queryWrapper = new QueryWrapper<UserDO>().eq("mail",email);
        List<UserDO> userList = userMapper.selectList(queryWrapper);
        return userList.size()>1?true:false;
    }
    @Data
    private class TokenClass{
            private String accessToken;
            private String refreshToken;

    }
}
