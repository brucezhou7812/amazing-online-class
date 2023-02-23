package nz.co.mapper;

import nz.co.model.ProductOrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-24
 */
public interface ProductOrderMapper extends BaseMapper<ProductOrderDO> {

    void updateOrderState(@Param("serialNo")String serialNo, @Param("oldState")String oldState,  @Param("newState")String newState);
}
