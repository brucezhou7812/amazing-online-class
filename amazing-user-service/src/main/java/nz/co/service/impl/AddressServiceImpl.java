package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import nz.co.mapper.AddressMapper;
import nz.co.model.AddressDO;
import nz.co.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2022-12-04
 */
@Service
public class AddressServiceImpl  implements AddressService {

@Autowired
private AddressMapper addressMapper;
    @Override
    public AddressDO detail(Long id) {
        AddressDO addressDO = addressMapper.selectOne(new QueryWrapper<AddressDO>().eq("id",id));
        return addressDO;
    }
}
