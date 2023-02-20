package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import nz.co.model.CartTaskDO;
import nz.co.mapper.CartTaskMapper;
import nz.co.service.CartTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-02-10
 */
@Service
public class CartTaskServiceImpl implements CartTaskService {
    @Autowired
    private CartTaskMapper cartTaskMapper;
    @Override
    public int insert(CartTaskDO cartTaskDO) {
        return cartTaskMapper.insert(cartTaskDO);
    }

    @Override
    public CartTaskDO listCartTask(String serialNo, Long productId,Long userId) {
        return cartTaskMapper.selectOne(new QueryWrapper<CartTaskDO>()
        .eq("product_id",productId)
        .eq("serial_num",serialNo)
        .eq("user_id",userId));
    }

    @Override
    public int updateLockState(CartTaskDO cartTaskDO) {
        return cartTaskMapper.update(cartTaskDO,new QueryWrapper<CartTaskDO>()
        .eq("product_id",cartTaskDO.getProductId())
        .eq("serial_num",cartTaskDO.getSerialNum()));
    }


}
