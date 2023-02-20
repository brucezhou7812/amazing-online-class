package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import nz.co.model.CouponTaskDO;
import nz.co.mapper.CouponTaskMapper;
import nz.co.service.CouponTaskService;
import nz.co.model.CouponTaskVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-29
 */
@Service
public class CouponTaskServiceImpl implements CouponTaskService {
    @Autowired
    private CouponTaskMapper couponTaskMapper;


    @Override
    public int updateLockState(CouponTaskDO couponTaskDO) {
        return couponTaskMapper.update(couponTaskDO,new QueryWrapper<CouponTaskDO>()
        .eq("id",couponTaskDO.getId()));
    }

    @Override
    public CouponTaskDO queryById(Long couponTaskId) {
        CouponTaskDO couponTaskDO = couponTaskMapper.selectById(couponTaskId);
        return couponTaskDO;
    }

    private CouponTaskVO beanProcess(CouponTaskDO couponTaskDO){
        if(couponTaskDO == null) return null;
        CouponTaskVO couponTaskVO = new CouponTaskVO();
        BeanUtils.copyProperties(couponTaskDO,couponTaskVO);
        return couponTaskVO;
    }
}
