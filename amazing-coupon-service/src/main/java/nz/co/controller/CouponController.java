package nz.co.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nz.co.enums.BizCodeEnum;
import nz.co.model.CouponRecordDO;
import nz.co.service.CouponService;
import nz.co.utils.JsonData;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-05
 */
@RestController
@RequestMapping("/api/coupon/v1")
@Api(tags="Coupon service")
@Slf4j
public class CouponController {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private CouponService couponService;
    @GetMapping("list_coupon_inpage")
    @ApiOperation(value="List Coupon in pages")
    public JsonData listCouponInPage(@ApiParam(value="current page")@RequestParam(value="pages",required = true) int pages,
                                     @ApiParam(value="records in each page")@RequestParam(value="size",required = true) int size){
        Map<String,Object> map = couponService.listCouponInPage(pages,size);
        return map.size()!=0 ? JsonData.buildSuccess(map):JsonData.buildResult(BizCodeEnum.COUPON_NOT_EXIST);
    }

    @PostMapping("receive_coupon/{coupon_id}")
    @ApiOperation("Receive a coupon through coupon id")
    public JsonData recevieCoupon(@ApiParam(value="coupon id")@PathVariable(value = "coupon_id")
                                  Long coupon_id){
         CouponRecordDO couponRecordDO = couponService.recevieCoupon(coupon_id);
        return JsonData.buildSuccess(couponRecordDO);
    }
    @GetMapping("lock")
    public JsonData testLock(){
        RLock rLock = redissonClient.getLock("lock:coupon:1");
        rLock.lock();
        try{
            log.info("succeed to lock :"+Thread.currentThread().getId());
            TimeUnit.SECONDS.sleep(20);
        }catch (Exception e){

        }finally {
            log.info("succeed to unlock :"+Thread.currentThread().getId());
            rLock.unlock();
        }
        return JsonData.buildSuccess(Thread.currentThread().getId());
    }
}

