package nz.co.service;

import nz.co.model.ProductTaskDO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-29
 */
public interface ProductTaskService{

    int insert(ProductTaskDO productTaskDO);

    ProductTaskDO queryTaskById(Long taskId);

    int update(ProductTaskDO productTaskDO);
}
