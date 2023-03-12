package nz.co.service;

import nz.co.model.CouponTaskDO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-29
 */
public interface CouponTaskService {



    int updateLockState(CouponTaskDO couponTaskDO);

    CouponTaskDO queryById(Long couponTaskId);
}
