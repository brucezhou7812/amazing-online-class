package nz.co.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nz.co.enums.BizCodeEnum;
import nz.co.service.CouponService;
import nz.co.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/api/coupon/v1")
@Api(tags="Coupon service")
public class CouponController {
    @Autowired
    private CouponService couponService;
    @GetMapping("list_coupon_inpage")
    @ApiOperation(value="List Coupon in pages")
    public JsonData listCouponInPage(@ApiParam(value="current page")@RequestParam(value="pages",required = true) int pages,
                                     @ApiParam(value="records in each page")@RequestParam(value="size",required = true) int size){
        Map<String,Object> map = couponService.listCouponInPage(pages,size);
        return map.size()!=0 ? JsonData.buildSuccess(map):JsonData.buildResult(BizCodeEnum.COUPON_NOT_EXIST);
    }

}

