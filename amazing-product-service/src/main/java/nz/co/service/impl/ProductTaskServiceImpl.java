package nz.co.service.impl;

import nz.co.model.ProductTaskDO;
import nz.co.mapper.ProductTaskMapper;
import nz.co.service.ProductTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class ProductTaskServiceImpl implements ProductTaskService {
    @Autowired
    private ProductTaskMapper productTaskMapper;
    @Override
    public int insert(ProductTaskDO productTaskDO) {
        return productTaskMapper.insert(productTaskDO);
    }
}
