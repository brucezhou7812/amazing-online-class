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
import nz.co.vo.AddressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


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
    public AddressVO detail(Long id) {
        AddressDO addressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>().eq("id",id));
        if(addressDO == null) return null;
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(addressDO,addressVO);
        return addressVO;
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

    @Override
    public int delete(Long address_id) {
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        QueryWrapper<AddressDO> queryWrapper = new QueryWrapper<AddressDO>()
                .eq("id",address_id)
                .eq("user_id",userLoginModel.getId());
        int row = addressMapper.delete(queryWrapper);
        return row;
    }

    @Override
    public List<AddressVO> listAll() {
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        QueryWrapper<AddressDO> queryWrapper = new QueryWrapper<AddressDO>()
                .eq("user_id",userLoginModel.getId());
        List<AddressDO> addressDOList = addressMapper.selectList(queryWrapper);
        List<AddressVO> addressVOList = addressDOList.stream().map(obj->{
            AddressVO addressVO = new AddressVO();
            BeanUtils.copyProperties(obj,addressVO);
            return addressVO;
        }).collect(Collectors.toList());
        return addressVOList;
    }
}
