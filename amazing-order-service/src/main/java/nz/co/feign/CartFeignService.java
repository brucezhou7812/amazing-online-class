package nz.co.feign;

import io.swagger.annotations.ApiParam;
import nz.co.model.CartItemVO;
import nz.co.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "amazing-produce-service")
public interface CartFeignService {
    @PostMapping(value="/api/cart/v1/confirm_items_in_cart")
    JsonData<List<CartItemVO>> confirmCartItems(@RequestBody List<Long> productIds);
}
