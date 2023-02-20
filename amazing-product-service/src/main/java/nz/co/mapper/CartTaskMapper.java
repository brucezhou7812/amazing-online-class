package nz.co.mapper;

import nz.co.model.CartTaskDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-02-10
 */
public interface CartTaskMapper extends BaseMapper<CartTaskDO> {

    int deleteByProductIdAndSerialNo(@Param("productId")Long productId, @Param("serialNo")String serailNo);
}
