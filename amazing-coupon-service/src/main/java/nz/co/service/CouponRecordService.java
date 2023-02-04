package nz.co.service;

import nz.co.model.CouponRecordDO;
import com.baomidou.mybatisplus.extension.service.IService;
import nz.co.model.CouponRecordMessage;
import nz.co.request.LockCouponRecordRequest;
import nz.co.utils.JsonData;
import nz.co.vo.CouponRecordVO;

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

    CouponRecordVO findRecordById(int record_id);

    JsonData lockCouponRecordBatch(LockCouponRecordRequest lockCouponRecordRequest);

    boolean releaseCouponRecord(CouponRecordMessage recordMessage);
}
