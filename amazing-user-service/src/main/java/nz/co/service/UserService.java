package nz.co.service;

import nz.co.model.UserDO;
import com.baomidou.mybatisplus.extension.service.IService;
import nz.co.request.UserLoginRequest;
import nz.co.request.UserRegisterRequest;
import nz.co.request.UserTokenRefreshRequest;
import nz.co.utils.JsonData;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2022-12-04
 */
public interface UserService{
    JsonData register(UserRegisterRequest userRegisterRequest);

    JsonData login(UserLoginRequest userLoginRequest);

    JsonData refresh(UserTokenRefreshRequest userTokenRefreshRequest);
}
