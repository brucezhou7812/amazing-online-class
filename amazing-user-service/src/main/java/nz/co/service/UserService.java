package nz.co.service;

import nz.co.model.UserDO;
import com.baomidou.mybatisplus.extension.service.IService;
import nz.co.request.UserRegisterRequest;
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
}
