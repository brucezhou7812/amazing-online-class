package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import nz.co.enums.AddressTypeEnun;
import nz.co.interceptor.LoginInterceptor;
import nz.co.mapper.AddressMapper;
import nz.co.model.AddressDO;
import nz.co.model.UserLoginModel;
import nz.co.request.AddressAddRequest;
import nz.co.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AddressServiceImpl  implements AddressService {

@Autowired
private AddressMapper addressMapper;
    @Override
    public AddressDO detail(Long id) {
        AddressDO addressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>().eq("id",id));

        return addressDO;
    }

    @Override
    public AddressDO add(AddressAddRequest addressAddRequest) {
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        Long userId = userLoginModel.getId();
        AddressDO addressDO = new AddressDO();
        BeanUtils.copyProperties(addressAddRequest,addressDO);
        addressDO.setCreateTime(new Date());
        if(addressDO.getDefaultStatus()==AddressTypeEnun.DEFAULT_ADDRESS.getCode()){
            QueryWrapper<AddressDO> queryWrapper = new QueryWrapper<AddressDO>()
                    .eq("user_id",userId)
                    .eq("default_status",AddressTypeEnun.DEFAULT_ADDRESS.getCode());
            AddressDO defaultAddress = addressMapper.selectOne(queryWrapper);
            if(defaultAddress != null) {
                defaultAddress.setDefaultStatus(AddressTypeEnun.COMMON_ADDRESS.getCode());
                addressMapper.update(defaultAddress, new QueryWrapper<AddressDO>().eq("id",defaultAddress.getId()));
                return defaultAddress;
            }
        }else{
            int rows = addressMapper.insert(addressDO);
            log.info("add address : "+rows);
            return addressDO;
        }
        return null;
    }
}
