package nz.co.mapper;

import nz.co.model.CouponRecordDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-05
 */
public interface CouponRecordMapper extends BaseMapper<CouponRecordDO> {

    int updateUserStateBatch(@Param(value = "couponRecordIds") List<Long> couponIds,@Param(value="useState") String useState,@Param(value="userId")Long userId);

    int updateUseState(@Param("couponRecordId")Long couponRecordId, @Param("useState")String useState);
}
