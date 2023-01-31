package nz.co.service;

import nz.co.enums.CouponCategoryEnum;
import nz.co.model.CouponRecordDO;
import nz.co.request.NewUserRequest;

import java.util.List;
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

    CouponRecordDO receiveCoupon(Long coupon_id, CouponCategoryEnum category);

    List<CouponRecordDO> receiveInitCoupon(NewUserRequest newUserRequest, CouponCategoryEnum couponCategoryNewUser);
}
