package nz.co.mapper;

import nz.co.model.ProductDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-20
 */
public interface ProductMapper extends BaseMapper<ProductDO> {

    int lockStock(@Param("id")Long id, @Param("buyNum")Integer buyNum);
}
