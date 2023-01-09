package nz.co.mapper;

import nz.co.model.CouponDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-05
 */
public interface CouponMapper extends BaseMapper<CouponDO> {

    int reduceStock(@Param("coupon_id")Long coupon_id);
}
