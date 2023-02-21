package nz.co.feign;

import io.swagger.annotations.ApiParam;
import nz.co.model.AddressVO;
import nz.co.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(value="amazing-user-service")
public interface AddressFeignService {
    @GetMapping("/api/address/v1/find/{address_id}")
    JsonData<AddressVO> detail(@ApiParam(value="Address id",required = true)
                                      @PathVariable("address_id")Long id);
}
