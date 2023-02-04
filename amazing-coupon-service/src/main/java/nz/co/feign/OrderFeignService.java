package nz.co.feign;

import nz.co.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="amazing-order-service")
public interface OrderFeignService {
    @GetMapping("/api/order/product_order/list")
    public JsonData queryOrderStateBySerialNo(@RequestParam("serial_no")String serialNo);
}
