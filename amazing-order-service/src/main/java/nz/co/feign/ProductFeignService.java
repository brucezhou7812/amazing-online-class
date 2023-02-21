package nz.co.feign;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nz.co.model.CartItemVO;
import nz.co.model.LockProductsRequest;
import nz.co.model.ProductVO;
import nz.co.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "amazing-product-service")
public interface ProductFeignService {
    @GetMapping("/api/product/v1/product_detail/{product_id}")
    JsonData<ProductVO> productDetail(@PathVariable(value="product_id") Long product_id);
    @PostMapping("/api/product/v1/lock_stock")
    JsonData lockProductStock(@RequestBody LockProductsRequest lockProductsRequest);
    @PostMapping("/api/cart/v1/confirm_items_in_cart")
    JsonData<List<CartItemVO>> confirmCartItems(@RequestBody List<Long> productIds, @RequestParam("serial_no") String serialNo);
}
