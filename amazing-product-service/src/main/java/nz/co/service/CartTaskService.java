package nz.co.service;

import nz.co.model.CartTaskDO;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-02-10
 */
public interface CartTaskService{

    int insert(CartTaskDO cartTaskDO);
    CartTaskDO listCartTask(String serialNo,Long productId,Long userId);


    int updateLockState(CartTaskDO cartTaskDO);
}
