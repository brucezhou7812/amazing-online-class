package nz.co.service.impl;

import lombok.extern.slf4j.Slf4j;
import nz.co.enums.BizCodeEnum;
import nz.co.enums.SendCodeEnum;
import nz.co.model.UserDO;
import nz.co.mapper.UserMapper;
import nz.co.request.UserRegisterRequest;
import nz.co.service.NotifyService;
import nz.co.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nz.co.utils.JsonData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    private void userRegisterInitTask(UserDO userDO){

    }
    private boolean checkMailUnique(String email){
        return false;
    }
}
