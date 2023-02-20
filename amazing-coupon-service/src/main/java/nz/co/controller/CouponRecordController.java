package nz.co.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nz.co.enums.BizCodeEnum;
import nz.co.model.CouponTaskDO;
import nz.co.model.LockCouponRecordRequest;
import nz.co.service.CouponRecordService;
import nz.co.utils.JsonData;
import nz.co.model.CouponRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-05
 */
@RestController
@RequestMapping("/api/couponrecord/v1")
@Api(tags="Coupon Record Service")
public class CouponRecordController {
    @Autowired
    private CouponRecordService couponRecordService;

    @GetMapping("page")
    @ApiOperation("List Coupon Records page by page")
    public JsonData page(@ApiParam("Current page number") @RequestParam(value = "page",defaultValue = "1") int page,
                         @ApiParam("How many records in a page") @RequestParam(value = "size",defaultValue = "10")int size){
        Map<String,Object> pageResult = new HashMap<String,Object>(3);
        pageResult = couponRecordService.page(page,size);
        return JsonData.buildSuccess(pageResult);
    }

    @GetMapping("detail/{record_id}")
    @ApiOperation("Query coupon record detail information")
    public JsonData<CouponRecordVO> detail(@ApiParam("Coupon record id")@PathVariable("record_id") Long record_id){
        CouponRecordVO couponRecordVO = couponRecordService.findRecordById(record_id);
        return couponRecordVO != null ? JsonData.buildSuccess(couponRecordVO):JsonData.buildResult(BizCodeEnum.COUPON_NOT_EXIST);
    }
    @PostMapping("lock_coupon_record_batch")
    @ApiOperation("RPC:lock coupon record batch")
    public JsonData<CouponTaskDO> lockCouponRecordBatch(@ApiParam("coupon record ids")@RequestBody LockCouponRecordRequest lockCouponRecordRequest){
        JsonData jsonData = couponRecordService.lockCouponRecordBatch(lockCouponRecordRequest);
        return jsonData;
    }



}

