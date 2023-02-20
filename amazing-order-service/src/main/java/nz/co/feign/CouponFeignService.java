package nz.co.feign;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nz.co.model.CouponRecordVO;
import nz.co.model.CouponTaskDO;
import nz.co.model.LockCouponRecordRequest;
import nz.co.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value="amazing-coupon-service")
public interface CouponFeignService {
    @GetMapping("/api/couponrecord/v1/detail/{record_id}")
    JsonData<CouponRecordVO> detail(@PathVariable("record_id") Long record_id);
    @PostMapping("/api/couponrecord/v1/lock_coupon_record_batch")
    JsonData<CouponTaskDO> lockCouponRecordBatch(@RequestBody LockCouponRecordRequest lockCouponRecordRequest);



}
