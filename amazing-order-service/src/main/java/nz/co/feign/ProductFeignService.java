package nz.co.feign;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nz.co.model.LockProductsRequest;
import nz.co.model.ProductVO;
import nz.co.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "amazing-produce-service")
public interface ProductFeignService {
    @GetMapping("/api/product/v1/product_detail/{product_id}")
    JsonData<ProductVO> productDetail(@PathVariable(value="product_id") Long product_id);
    @PostMapping("/api/product/v1/lock_stock")
    JsonData lockProductStock(@RequestBody LockProductsRequest lockProductsRequest);
}
