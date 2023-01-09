package nz.co.service;

import nz.co.model.CouponDO;
import com.baomidou.mybatisplus.extension.service.IService;
import nz.co.model.CouponRecordDO;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-05
 */
public interface CouponService{

    Map<String,Object> listCouponInPage(int pages, int size);

    CouponRecordDO recevieCoupon(Long coupon_id);
}
