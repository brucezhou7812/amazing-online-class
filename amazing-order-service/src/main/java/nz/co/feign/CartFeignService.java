package nz.co.feign;

import io.swagger.annotations.ApiParam;
import nz.co.model.CartItemVO;
import nz.co.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
/*
@FeignClient(value = "amazing-product-service")
public interface CartFeignService {
    @PostMapping("/api/cart/v1/confirm_items_in_cart")
    JsonData<List<CartItemVO>> confirmCartItems(@RequestBody List<Long> productIds,@RequestParam("serial_no") String serialNo);
}

 */
