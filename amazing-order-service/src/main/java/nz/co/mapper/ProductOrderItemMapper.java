package nz.co.mapper;

import nz.co.model.ProductOrderItemDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-24
 */
public interface ProductOrderItemMapper extends BaseMapper<ProductOrderItemDO> {
    int insertBatch(@Param(value="productOrderItemDOs")List<ProductOrderItemDO> productOrderItemDOs);
}
