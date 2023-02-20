package nz.co.service;

import nz.co.model.CouponRecordMessage;
import nz.co.model.CouponTaskDO;
import nz.co.model.LockCouponRecordRequest;
import nz.co.utils.JsonData;
import nz.co.model.CouponRecordVO;

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

    CouponRecordVO findRecordById(Long record_id);

    JsonData<CouponTaskDO> lockCouponRecordBatch(LockCouponRecordRequest lockCouponRecordRequest);

    boolean releaseCouponRecord(CouponRecordMessage recordMessage);
}
