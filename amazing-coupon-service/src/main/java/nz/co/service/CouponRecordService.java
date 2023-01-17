package nz.co.service;

import nz.co.model.CouponRecordDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-05
 */
public interface CouponRecordService {

    Map<String, Object> page(int page, int size);
}
