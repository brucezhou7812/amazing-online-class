package nz.co.feign;

import io.swagger.annotations.ApiParam;
import nz.co.request.NewUserRequest;
import nz.co.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="amazing-coupon-service")
public interface CouponFeignService {
    @PostMapping("/api/coupon/v1/add_new_user_coupon")
    JsonData addNewUserCoupon(@RequestBody NewUserRequest newUserRequest);
}
