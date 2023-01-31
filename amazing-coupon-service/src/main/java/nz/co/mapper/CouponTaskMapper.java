package nz.co.mapper;

import nz.co.model.CouponTaskDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-29
 */
public interface CouponTaskMapper extends BaseMapper<CouponTaskDO> {

    int insertBatch(@Param("couponTaskDOs") List<CouponTaskDO> couponTaskDOs);
}
